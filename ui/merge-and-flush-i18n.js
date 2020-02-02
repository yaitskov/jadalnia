const fPath = require('path');
const fs = require('fs');

class MergeAndFlushI18nPlugin {
  constructor(state) {
    this.transFolder = 'i18n-dicts';
    this.state = state;
  }

  apply(compiler) {
    compiler.hooks.afterCompile.tap('Merge-And-Flush i18n Plugin', stats => {
      this.doMergeAndFlush();
    });
  }

  doMergeAndFlush() {
    const rootFiles = this.findRootFiles();
    this.checkStaticRootsAndBundleNames(rootFiles);
    this.mkdir(this.transFolder);
    this.bundlAndFlushBundleDicts(rootFiles);
  }

  logInputData() {
    console.log(`Flush dynamic import graph:\n${JSON.stringify(this.state.dynamicImports.sourceVertexes, null, 2)}`);
    console.log(`Flush static import graph:\n${JSON.stringify(this.state.staticImports.sourceVertexes, null, 2)}`);
    console.log(`Flush translation dictionaries:\n${JSON.stringify(this.state.dicts.dicts, null, 2)}`);
  }

  findReachableFiles(rootFile, accumulator) {
    const reachable = this.state.staticImports.r(rootFile);
    accumulator.add(rootFile);
    if (reachable) {
      reachable.forEach(file => {
        this.findReachableFiles(file, accumulator);
      });
    }
  }

  combineDictionaries(files) {
    const combined = {};
    [...files].forEach(file => {
      const fileDictEntries = this.state.dicts.entries(file);
      for (let entry of fileDictEntries) {
        combined[entry[0]] = entry[1];
      }
    });
    return combined;
  }

  flushDictionary(rootFile, dictionary) {
    const folder = `${this.transFolder}/${fPath.basename(rootFile)}`;
    this.mkdir(folder);
    fs.writeFileSync(`${folder}/index.json`, JSON.stringify(dictionary));
  }

  mkdir(folder) {
    if (!fs.existsSync(folder)) {
      fs.mkdirSync(folder);
    }
  }

  findDynamicRootFiles() {
    const result = new Set();
    for (let srcEntry of this.state.dynamicImports.entries()) {
      srcEntry[1].forEach(dstFile => result.add(dstFile));
    }
    return result;
  }

  checkStaticRootsAndBundleNames(rootFiles) {
    const dynamicRoots = this.findDynamicRootFiles();
    const staticRootFiles = [...rootFiles]
      .filter(file => !dynamicRoots.has(file))
      .filter(file => !file.startsWith("worker/")); // skip service workers

    if (staticRootFiles.length !== 1) {
      throw new Error(
        `expected 1 static root file but got ${JSON.stringify(staticRootFiles)}.
        Check that static imports having absolute paths.`);
    }

    const uniqueBaseNames = new Set([...rootFiles].map(file => fPath.basename(file)));

    if (uniqueBaseNames.size != rootFiles.size) {
      throw new Error(
        `base names of some root files are umbigous: ${JSON.stringify([...rootFiles])}`);
    }
  }

  bundlAndFlushBundleDicts(rootFiles) {
    for (let rootFile of rootFiles) {
      const reachableFiles = new Set();
      this.findReachableFiles(rootFile, reachableFiles);
      const dictionary = this.combineDictionaries(reachableFiles);
      this.flushDictionary(rootFile, dictionary);
    }
  }

  findRootFiles() {
    const dstFiles = new Set();
    for (let srcEntry of this.state.staticImports.entries()) {
      srcEntry[1].forEach(dstFile => dstFiles.add(dstFile));
    }

    return new Set(this.state.staticImports
                   .sources()
                   .filter(file => !dstFiles.has(file)));
  }
}

module.exports = MergeAndFlushI18nPlugin;
