package org.dan.jadalnia.sys.ctx

import org.dan.jadalnia.sys.async.AsynSync
import org.springframework.context.annotation.Import

@Import(AsynSync::class)
class AsyncCtx
