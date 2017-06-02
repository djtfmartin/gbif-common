package org.gbif.utils.file.tabular;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Internal {@link TabularDataFileReader} implementation backed by Super CSV.
 */
class SuperCsvFileReader implements TabularDataFileReader<List<String>> {

  //CsvListReader was chosen to allow the reading of a line with more (or less) data than declared (by the headers).
  private final CsvListReader csvListReader;
  private final boolean headerLineIncluded;

  private List<String> headerLine;
  private boolean headerLineRead = false;

  /**
   * Package protected constructor.
   *
   * @param in
   * @param quoteChar
   * @param delimiterChar
   * @param endOfLineSymbols
   * @param charset
   * @param headerLineIncluded
   */
  SuperCsvFileReader(InputStream in, char quoteChar, char delimiterChar, String endOfLineSymbols,
                            Charset charset, boolean headerLineIncluded){

    Objects.requireNonNull(in, "InputStream shall be provided");
    Objects.requireNonNull(endOfLineSymbols, "endOfLineSymbols shall be provided");

    CsvPreference.Builder builder = new CsvPreference.Builder(quoteChar, delimiterChar, endOfLineSymbols)
            .ignoreEmptyLines(true);
    csvListReader = new CsvListReader(new InputStreamReader(in, charset), builder.build());
    this.headerLineIncluded = headerLineIncluded;
    headerLineRead = !headerLineIncluded;
  }

  @Override
  public List<String> getHeaderLine() throws IOException {
    if (headerLineIncluded && !headerLineRead) {
      headerLine = csvListReader.read();
      headerLineRead = true;
    }
    return headerLine;
  }

  /**
   * Read a line of the tabular data file.
   *
   * @return the line as List or null if the end of the file is reached.
   * @throws IOException
   */
  public List<String> read() throws IOException {
    if (headerLineRead) {
      return csvListReader.read();
    }
    headerLine = csvListReader.read();
    headerLineRead = true;
    return csvListReader.read();
  }

  @Override
  public void close() throws IOException {
    if (csvListReader != null) {
        csvListReader.close();
    }
  }
}
