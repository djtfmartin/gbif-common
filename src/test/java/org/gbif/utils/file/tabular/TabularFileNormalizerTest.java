/***************************************************************************
 * Copyright 2017 Global Biodiversity Information Facility Secretariat
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ***************************************************************************/

package org.gbif.utils.file.tabular;

import org.gbif.utils.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static junit.framework.TestCase.assertEquals;

/**
 * Unit test related to {@link TabularFileNormalizer}
 */
public class TabularFileNormalizerTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testTabularFileNormalizer() throws IOException {
    //this file includes a null character (\0) that is expected to be removed
    File csvFile = FileUtils.getClasspathFile("tabular/test_normalize.csv");
    File normalizedFile = folder.newFile();

    int numberOfLine = TabularFileNormalizer.normalizeFile(
            csvFile.toPath(), normalizedFile.toPath(), StandardCharsets.UTF_8, ',', "\n", '\"');

    List<String> rows = org.apache.commons.io.FileUtils.readLines(normalizedFile, StandardCharsets.UTF_8);
    assertEquals("Quoted delimiter", "1,\"a,\",b", rows.get(0));
    assertEquals("Trailing newline", "2,c,d", rows.get(1));
    assertEquals("Quoted non-ASCII and null character", "3,é,f", rows.get(2));
    assertEquals("Long values", "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986,\"Pi, Pi, Pi, Pi, Pi, Pi, Pi, Pi\",ππππππππππππππππππππππππππππππππππππππππππππππ", rows.get(3));
    assertEquals(4, numberOfLine);
  }
}
