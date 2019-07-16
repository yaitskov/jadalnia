package org.dan.jadalnia.sys.ctx;

import org.dan.jadalnia.util.time.NowClocker;
import org.springframework.context.annotation.Import;

@Import(NowClocker.class)
public class TimeContext {}
