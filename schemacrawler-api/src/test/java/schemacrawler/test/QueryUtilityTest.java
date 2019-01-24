/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.Query;
import schemacrawler.utility.QueryUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class QueryUtilityTest
{

  @Test
  public void executeAgainstSchema(final TestContext testContext,
                                   final Connection cxn)
    throws Exception
  {

    final Query query = new Query("Tables for schema",
                                  "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE REGEXP_MATCHES(TABLE_SCHEMA, '${schemas}') ORDER BY TABLE_NAME");
    final InclusionRule schemaInclusionRule = new RegularExpressionInclusionRule("BOOKS");

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      try (final Connection connection = cxn;
          final Statement statement = connection.createStatement();
          final ResultSet resultSet = QueryUtility
            .executeAgainstSchema(query, statement, schemaInclusionRule);)
      {
        while (resultSet.next())
        {
          out.println(resultSet.getString("TABLE_NAME"));
        }
      }

    }
    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(testContext
                 .testMethodFullName())));
  }

}
