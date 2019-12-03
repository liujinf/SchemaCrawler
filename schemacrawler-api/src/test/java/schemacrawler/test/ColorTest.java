/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sf.util.Color;

public class ColorTest
{
  @Test
  public void fromHSV()
  {
    assertThat(Color.fromHSV(0, 0, 0).toString(), is("#000000"));

    assertThat(Color.fromHSV(0, 1, 0).toString(), is("#000000"));
    assertThat(Color.fromHSV(0, -1, 0).toString(), is("#000000"));
    assertThat(Color.fromHSV(0, 0, 1).toString(), is("#FFFFFF"));
    assertThat(Color.fromHSV(0, 0, -1).toString(), is("#000000"));

    assertThat(Color.fromHSV(1, 1, 0).toString(), is("#000000"));
    assertThat(Color.fromHSV(1, 1, 0.2f).toString(), is("#330000"));
    assertThat(Color.fromHSV(1, 0.2f, 0.2f).toString(), is("#332929"));
    assertThat(Color.fromHSV(0.2f, 0.2f, 0.2f).toString(), is("#313329"));
    assertThat(Color.fromHSV(-0.2f, 0.2f, 0.2f).toString(), is("#312933"));
    assertThat(Color.fromHSV(0.2f, -0.2f, 0.2f).toString(), is("#35333D"));
    assertThat(Color.fromHSV(0.2f, 0.2f, -0.2f).toString(), is("#000000"));
  }

  @Test
  public void fromHexTriplet()
  {
    assertThat(Color.fromHexTriplet("#010203").toString(), is("#010203"));

    assertThrows(IllegalArgumentException.class,
                 () -> Color.fromHexTriplet(null));
    assertThrows(IllegalArgumentException.class,
                 () -> Color.fromHexTriplet(""));
    assertThrows(IllegalArgumentException.class,
                 () -> Color.fromHexTriplet(" "));
    assertThrows(IllegalArgumentException.class,
                 () -> Color.fromHexTriplet("123456"));
    assertThrows(IllegalArgumentException.class,
                 () -> Color.fromHexTriplet("#1234567"));
    assertThrows(IllegalArgumentException.class,
                 () -> Color.fromHexTriplet("#12345"));
  }

}
