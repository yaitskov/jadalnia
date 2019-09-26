package org.dan.jadalnia.sys.type.number

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class ImmutableNumberTest {
  @Test
  fun equals() = assertThat(ImmutableNumber(1)).isEqualTo(ImmutableNumber(1))
}
