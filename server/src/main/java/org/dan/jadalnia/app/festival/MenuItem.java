package org.dan.jadalnia.app.festival;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private DishName name;
    private String description;
    private double price;
    private boolean disabled;
    private List<MenuItem> additions;
}
