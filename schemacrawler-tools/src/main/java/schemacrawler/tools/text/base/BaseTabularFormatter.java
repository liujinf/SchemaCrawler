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

package schemacrawler.tools.text.base;


import static sf.util.Utility.isBlank;

import java.util.Collection;

import schemacrawler.SchemaCrawlerInfo;
import schemacrawler.schema.*;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.text.utility.html.Alignment;
import sf.util.ObjectToString;

/**
 * Text formatting of schema.
 *
 * @author Sualeh Fatehi
 */
public abstract class BaseTabularFormatter<O extends BaseTextOptions>
  extends BaseFormatter<O>
{

  protected BaseTabularFormatter(final O options,
                                 final boolean printVerboseDatabaseInfo,
                                 final OutputOptions outputOptions,
                                 final String identifierQuoteString)
    throws SchemaCrawlerException
  {
    super(options,
          printVerboseDatabaseInfo,
          outputOptions,
          identifierQuoteString);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void begin()
  {
    if (!options.isNoHeader())
    {
      formattingHelper.writeDocumentStart();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void end()
    throws SchemaCrawlerException
  {
    if (!options.isNoFooter())
    {
      formattingHelper.writeDocumentEnd();
    }

    super.end();
  }

  @Override
  public void handle(final CrawlInfo crawlInfo)
  {
    if (crawlInfo == null)
    {
      return;
    }

    final String title = outputOptions.getTitle();
    if (!isBlank(title))
    {
      formattingHelper.writeHeader(DocumentHeaderType.title, title);
    }

    if (options.isNoInfo())
    {
      return;
    }

    formattingHelper
      .writeHeader(DocumentHeaderType.subTitle, "System Information");

    formattingHelper.writeObjectStart();

    if (!options.isNoSchemaCrawlerInfo())
    {
      formattingHelper.writeNameValueRow("generated by",
                                         crawlInfo.getSchemaCrawlerInfo(),
                                         Alignment.inherit);
      formattingHelper.writeNameValueRow("generated on",
                                         formatTimestamp(crawlInfo
                                                           .getCrawlTimestamp()),
                                         Alignment.inherit);
    }

    if (options.isShowDatabaseInfo())
    {
      formattingHelper.writeNameValueRow("database version",
                                         crawlInfo.getDatabaseInfo(),
                                         Alignment.inherit);
    }

    if (options.isShowJdbcDriverInfo())
    {
      formattingHelper.writeNameValueRow("driver version",
                                         crawlInfo.getJdbcDriverInfo(),
                                         Alignment.inherit);
    }

    formattingHelper.writeObjectEnd();
  }

  @Override
  public final void handle(final DatabaseInfo dbInfo)
  {
    if (!printVerboseDatabaseInfo || !options.isShowDatabaseInfo()
        || dbInfo == null)
    {
      return;
    }

    final Collection<Property> serverInfo = dbInfo.getServerInfo();
    if (!serverInfo.isEmpty())
    {
      formattingHelper
        .writeHeader(DocumentHeaderType.section, "Database Server Information");
      formattingHelper.writeObjectStart();
      for (final Property property : serverInfo)
      {
        final String name = property.getName();
        Object value = property.getValue();
        if (value == null)
        {
          value = "";
        }
        formattingHelper.writeNameValueRow(name,
                                           ObjectToString.toString(value),
                                           Alignment.inherit);
      }
      formattingHelper.writeObjectEnd();
    }

    formattingHelper
      .writeHeader(DocumentHeaderType.section, "Database Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow("database product name",
                                       dbInfo.getProductName(),
                                       Alignment.inherit);
    formattingHelper.writeNameValueRow("database product version",
                                       dbInfo.getProductVersion(),
                                       Alignment.inherit);
    formattingHelper.writeNameValueRow("database user name",
                                       dbInfo.getUserName(),
                                       Alignment.inherit);
    formattingHelper.writeObjectEnd();

    final Collection<DatabaseProperty> dbProperties = dbInfo.getProperties();
    if (!dbProperties.isEmpty())
    {
      formattingHelper
        .writeHeader(DocumentHeaderType.section, "Database Characteristics");
      formattingHelper.writeObjectStart();
      for (final DatabaseProperty property : dbProperties)
      {
        final String name = property.getDescription();
        Object value = property.getValue();
        if (value == null)
        {
          value = "";
        }
        formattingHelper.writeNameValueRow(name,
                                           ObjectToString.toString(value),
                                           Alignment.inherit);
      }
      formattingHelper.writeObjectEnd();
    }
  }

  @Override
  public void handle(final JdbcDriverInfo driverInfo)
  {
    if (!printVerboseDatabaseInfo || !options.isShowJdbcDriverInfo()
        || driverInfo == null)
    {
      return;
    }

    formattingHelper
      .writeHeader(DocumentHeaderType.section, "JDBC Driver Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow("driver name",
                                       driverInfo.getProductName(),
                                       Alignment.inherit);
    formattingHelper.writeNameValueRow("driver version",
                                       driverInfo.getProductVersion(),
                                       Alignment.inherit);
    formattingHelper.writeNameValueRow("driver class name",
                                       driverInfo.getDriverClassName(),
                                       Alignment.inherit);
    formattingHelper.writeNameValueRow("url",
                                       driverInfo.getConnectionUrl(),
                                       Alignment.inherit);
    formattingHelper.writeNameValueRow("is JDBC compliant",
                                       Boolean.toString(driverInfo
                                                          .isJdbcCompliant()),
                                       Alignment.inherit);
    formattingHelper.writeObjectEnd();

    final Collection<JdbcDriverProperty> jdbcDriverProperties = driverInfo
      .getDriverProperties();
    if (!jdbcDriverProperties.isEmpty())
    {
      formattingHelper
        .writeHeader(DocumentHeaderType.section, "JDBC Driver Properties");
      for (final JdbcDriverProperty driverProperty : jdbcDriverProperties)
      {
        formattingHelper.writeObjectStart();
        printJdbcDriverProperty(driverProperty);
        formattingHelper.writeObjectEnd();
      }
    }
  }

  @Override
  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
  {
    if (!printVerboseDatabaseInfo || options.isNoSchemaCrawlerInfo()
        || schemaCrawlerInfo == null)
    {
      return;
    }

    formattingHelper
      .writeHeader(DocumentHeaderType.section, "SchemaCrawler Information");

    formattingHelper.writeObjectStart();
    formattingHelper.writeNameValueRow("product name",
                                       schemaCrawlerInfo.getProductName(),
                                       Alignment.inherit);
    formattingHelper.writeNameValueRow("product version",
                                       schemaCrawlerInfo.getProductVersion(),
                                       Alignment.inherit);
    formattingHelper.writeObjectEnd();
  }

  @Override
  public final void handleHeaderEnd()
    throws SchemaCrawlerException
  {

  }

  @Override
  public final void handleHeaderStart()
    throws SchemaCrawlerException
  {

  }

  @Override
  public final void handleInfoEnd()
    throws SchemaCrawlerException
  {

  }

  @Override
  public final void handleInfoStart()
    throws SchemaCrawlerException
  {
    if (!printVerboseDatabaseInfo || options.isNoInfo())
    {
      return;
    }

    formattingHelper
      .writeHeader(DocumentHeaderType.subTitle, "System Information");
  }

  private void printJdbcDriverProperty(final JdbcDriverProperty driverProperty)
  {
    final String required =
      (driverProperty.isRequired()? "": "not ") + "required";
    String details = required;
    if (driverProperty.getChoices() != null && !driverProperty.getChoices()
      .isEmpty())
    {
      details = details + "; choices " + driverProperty.getChoices();
    }
    final String value = driverProperty.getValue();

    formattingHelper
      .writeNameRow(driverProperty.getName(), "[driver property]");
    formattingHelper.writeDescriptionRow(driverProperty.getDescription());
    formattingHelper.writeDescriptionRow(details);
    formattingHelper.writeDetailRow("", "value", value);
  }
}
