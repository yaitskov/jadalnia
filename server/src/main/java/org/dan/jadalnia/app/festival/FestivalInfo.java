package org.dan.jadalnia.app.festival;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;

import java.time.Instant;
import java.util.List;

@Getter
@Wither
@Builder
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class FestivalInfo {
    Fid fid;
    String name;
    FestivalState state;
    List<MenuItem> menu;
    Instant opensAt;
}
