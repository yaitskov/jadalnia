package org.dan.jadalnia.app.festival.menu

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.dan.jadalnia.sys.jackson.ObjectMapperFactory
import org.junit.Test

class MenuItemTest {
  val om = ObjectMapperFactory.create()

  val v = MenuItem(
      name = DishName("dn"),
      description = "desc",
      price = 1.0,
      disabled = false,
      additions = emptyList())

  val jsonWithoutListField
      = """{"name":"danie","description":"smacznę","price":1.0,"disabled":false}"""

  val jsonWithNullListField
      = """{"name":"danie","description":"smacznę","price":1.0,"disabled":false,"additions":null}"""

  @Test
  fun deserializeFromJson() {
    assertThat(om.readValue(om.writeValueAsString(v), MenuItem::class.java).additions)
        .isEqualTo(emptyList());
  }

  @Test
  fun deserializeFromJsonWithoutListField() {
    assertThat(om.readValue(jsonWithoutListField, MenuItem::class.java).additions)
        .isEqualTo(emptyList());
  }

  @Test
  fun deserializeFromJsonWithNulltListField() {
    assertThat(om.readValue(jsonWithNullListField, MenuItem::class.java).additions)
        .isEqualTo(emptyList());
  }
}