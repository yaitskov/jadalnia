"use strict";

const fs = require('fs');
const fPath = require('path');

Object.defineProperty(exports, "__esModule", {
  value: true
});

function json(o) {
  return JSON.stringify(o, null, 2);
}

class DAG {
  constructor() {
    this.sourceVertexes = {};
  }

  newEdge(src, dst) {
    let vertex = this.sourceVertexes[src];
    if (vertex) {
      if (vertex.indexOf(dst) < 0) {
        vertex.push(dst);
      }
    } else {
      this.sourceVertexes[src] = vertex = [dst];
    }
  }

  sources() {
    return Object.keys(this.sourceVertexes);
  }

  entries() {
    return Object.entries(this.sourceVertexes);
  }

  r(src) {
    return this.sourceVertexes[src];
  }
}

class Dictionaries {
  constructor() {
    this.dicts = {};
  }

  newEntry(fileName, msgId, paramNames, path, state) {
    let fileDict = this.dicts[fileName];
    if (!fileDict) {
      this.dicts[fileName] = fileDict = {};
    }
    let existingParams = fileDict[msgId];
    if (existingParams) {
      if (json(existingParams) != json(paramNames)) {
        throw fatalErr(
          `Params of message ${fileName}:${msgId} doesn't match:\n${json(existingParams)}\nvs:\n${json(paramNames)}`,
          path, state);
      }
    } else {
      fileDict[msgId] = paramNames;
    }
  }

  entries(file) {
    return Object.entries(this.dicts[file] || {});
  }
}

function newState() {
  return {
    dynamicImports: new DAG(),
    staticImports: new DAG(),
    dicts: new Dictionaries()
  };
}

function enforceRightSlash(s) {
  return s.replace(/\\+/g, '/');
}

function normalizedPath(state, params) {
  return enforceRightSlash(state.file.opts.filename.substring(params.rootPrefixLength).replace(/[.]tsx$/, ''));
}

function expandPath(source, mayBeLocalDest) {
  if (mayBeLocalDest.startsWith('.')) {
    return enforceRightSlash(fPath.normalize(`${fPath.dirname(source)}/${mayBeLocalDest}`));
  } else {
    return mayBeLocalDest;
  }
}

function fatalErr(msg, path, state) {
  return new Error(`${msg};
 Line ${json(path.node.loc)}
 in file ${state.file.opts.filename}`);
}

class JsxTranslator {
  constructor(result, params, types) {
    this.result = result;
    this.params = params;
    this.types = types;
    this.cleanAttributeIndex();
  }

  translate(path, state) {
    const oEl = path.node.openingElement;
    if (oEl.name.name === this.params.tagName) {
      this.translationTag(oEl, path, state);
    } else {
      this.translationAttribute(oEl, path, state);
    }
  }

  newDictEntry(path, state, msgTemplate, msgTemplateParamNames) {
    this.result.dicts.newEntry(
      normalizedPath(state, this.params),
      msgTemplate,
      Object.keys(msgTemplateParamNames)
        .map(k => {
          if (k.length > 5) {
            throw fatalErr(`message parameter ${k} is too long. Max 5 letters`, path, state);
          }
          return k;
        }).filter(k => k !== 'm').sort(),
      path,
      state);
  }

  translationTag(oEl, path, state) {
    const attrIndex = this.indexAttrsByName(oEl.attributes);
    const msgTplAttr = attrIndex['m'];
    if (this.checkMsgTplAttr(msgTplAttr, path, state)) {
      this.newDictEntry(
        path, state,
        msgTplAttr.value.value,
        attrIndex);
    }
  }

  forwardsTranslatedProperty(attr) {
    if (attr.value.type === 'JSXExpressionContainer') {
      const exp = attr.value.expression;
      if (exp.type === 'MemberExpression') {
        if (exp.object.type === 'MemberExpression') {
          if (exp.object.object.type === 'ThisExpression') {
            if (exp.object.property.type === 'Identifier') {
              if (exp.property.type === 'Identifier') {
                if (exp.property.name.match(/^t[$].+$/)) {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  translationAttribute(oEl, path, state) {
    for (let attr of oEl.attributes) {
      const match = attr.name.name.match(/^(t[$].+)$/);
      if (match) {
        if (attr.value.type != 'StringLiteral') {
          if (this.forwardsTranslatedProperty(attr)) {
            return;
          }
          throw fatalErr(
            `non literal values for translatable attributes (prefixes with t$) allowed only if this.props.t$...`,
            path, state);
        }
        const idx = this.newTransAttribute(path, state, attr.value.value);
        // attr.name = this.types.jsxIdentifier(match[1]);
        attr.value = this.types.jsxExpressionContainer(
          this.types.memberExpression(
            this.types.memberExpression(
              this.types.memberExpression(
                this.types.thisExpression(),
                this.types.identifier("st"),
                false),
              this.types.identifier("at"),
              false),
            this.types.numericLiteral(idx),
            true));
        // replace literal with {}name
      }
    }
  }

  newTransAttribute(path, state, attrText) {
    const existingIdx = this.idIndex[attrText];
    if (existingIdx !== undefined) {
      return existingIdx;
    }
    const newIdx = this.idIndex[attrText] = this.foundPhrases.length;
    this.newDictEntry(path, state, attrText, []);
    this.foundPhrases.push(attrText);
    return newIdx;
  }

  cleanAttributeIndex() {
    this.idIndex = {};
    this.foundPhrases = [];
  }

  ensureAttributeIndexFlushed(path, state) {
    if (this.foundPhrases.length) {
      throw fatalErr(
        `Declair empty [at] method after all tag methods. Found phrases: ${this.foundPhrases.join(', ')}.`,
        path, state);
    }
  }

  generateInitAtBody(path, state) {
    if (this.foundPhrases.length === 0) {
      return;
    }

    path.node.body = this.types.blockStatement(
      [
        this.types.returnStatement(
          this.types.arrayExpression(
            this.foundPhrases.map(
              phrase => this.types.stringLiteral(phrase))))
      ]);
    this.cleanAttributeIndex();
  }

  checkMsgTplAttr(msgTplAttr, path, state) {
    if (!msgTplAttr) {
      throw fatalErr(
        `${this.params.tagName} tag must have attribute (m) for message template`,
        path, state);
    }
    if (msgTplAttr.value.type === 'JSXExpressionContainer') {
      // skill variable message template
      return false;
    } else if (msgTplAttr.value.type !== 'StringLiteral') {
      throw fatalErr(
        `m attribute must be string literal or expression but got ${msgTplAttr.value.type}`,
        path, state);
    }
    return true;
  }

  indexAttrsByName(attrs) {
    const result = {};
    attrs.forEach(attr => result[attr.name.name] = attr);
    return result;
  }
}

class BundleTranslator {
  constructor(result, params) {
    this.result = result;
    this.params = params;
    this.insideAwait = false;
  }

  translateCall(path, state) {
    if (!this.insideAwait) {
      return;
    }
    const arg = path.node;
    if (arg.callee.type === 'Import') {
      if (arg.arguments.length === 1) {
        if (arg.arguments[0].type === 'StringLiteral') {
          const source = normalizedPath(state, this.params);
          this.result.dynamicImports.newEdge(
            source,
            expandPath(source, arg.arguments[0].value));
        } else {
          throw fatalErr(`dynamic import accepts just literal string`,
                         path, state);
        }
      } else {
        throw fatalErr(
          `only 1 literal string is supported, but ${json(arg.arguments)}.`,
          path, state);
      }
    } else if (arg.callee.type === 'MemberExpression') {
      // skip ok
    } else {
      console.log(`skip callexpression with calle ${arg.callee.type}`);
    }
  }

  translateImport(path, state) {
    if (path.node.source.type === 'StringLiteral') {
      const source = normalizedPath(state, this.params);
      this.result.staticImports.newEdge(
        source, (source, path.node.source.value));
    } else {
      throw fatalErr(
        `import must have literal string argument but ${path.node.source.type}`,
        path, state);
    }
  }

  enterAwait() {
    this.insideAwait = true;
  }

  leaveAwait() {
    this.insideAwait = false;
  }
}

function makeVisitorFactory(result, params) {
  return ({types}) => {
    params.tagName = params.tagName || 'TI';

    const jsxTranslator = new JsxTranslator(result, params, types);
    const bundleTranslator = new BundleTranslator(result, params);

    return {
      visitor: {
        ImportDeclaration: {
          enter(path, state) {
            bundleTranslator.translateImport(path, state);
          }
        },
        ClassMethod: {
          enter(path, state) {
            if (path.node.key.name === 'at') {
              jsxTranslator.generateInitAtBody(path, state);
            } else {
              // console.log(`skip method ${path.node.key.name}`);
            }
          }
        },
        ClassDeclaration: {
          exit(path, state) {
            jsxTranslator.ensureAttributeIndexFlushed(path, state);
          }
        },
        CallExpression: {
          enter(path, state) {
            bundleTranslator.translateCall(path, state);
          }
        },
        AwaitExpression: {
          enter() {
            bundleTranslator.enterAwait();
          },
          exit() {
            bundleTranslator.leaveAwait();
          }
        },
        JSXElement: {
          enter(path, state) {
            jsxTranslator.translate(path, state);
          }
        }
      }
    };
  };
}

module.exports = { makeVisitorFactory, newState };
