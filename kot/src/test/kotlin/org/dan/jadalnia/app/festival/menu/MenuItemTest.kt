package org.dan.jadalnia.app.festival.menu

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.fasterxml.jackson.core.type.TypeReference
import org.dan.jadalnia.sys.jackson.ObjectMapperFactory
import org.junit.Test
// import java.util.List

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

  val jsonWithNullableStringNull
      = """{"name":"danie","description":null,"price":1.0,"disabled":false}"""

  @Test
  fun deserializeFromJson() {
    assertThat(om.readValue(om.writeValueAsString(v), MenuItem::class.java).additions)
        .isEqualTo(emptyList())
  }

  @Test
  fun deserializeFromJsonWithoutListField() {
    assertThat(om.readValue(jsonWithoutListField, MenuItem::class.java).additions)
        .isEqualTo(emptyList())
  }

  @Test
  fun deserializeFromJsonWithNulltListField() {
    assertThat(om.readValue(jsonWithNullListField, MenuItem::class.java).additions)
        .isEqualTo(emptyList())
  }

  @Test
  fun deserializeFromJsonWithNullString() {
    assertThat(om.readValue(jsonWithNullableStringNull, MenuItem::class.java).description)
        .isNull()
  }

  val listOf1WithNullList
      = """[{"name":"danie","description":"smacznę","price":1.0,"disabled":false,"additions":null}]"""

  class X : TypeReference<List<MenuItem>>() {}

  @Test
  fun deserializeListOf1WithNull() {
    assertThat(om.readValue<List<MenuItem>>(listOf1WithNullList, X())[0].additions)
        .isEqualTo(emptyList())
  }
}