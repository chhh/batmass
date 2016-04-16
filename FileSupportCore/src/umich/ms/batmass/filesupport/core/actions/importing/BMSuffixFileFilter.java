/* 
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umich.ms.batmass.filesupport.core.actions.importing;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;

/**
 *
 * @author Dmitry Avtonomov
 */
public class BMSuffixFileFilter extends BMFileFilter {
    protected String shortDesc;
    protected String desc;
    protected String ext;

    public BMSuffixFileFilter(String ext, String shortDesc, String desc) {
        super(FileFilterUtils.suffixFileFilter(ext, IOCase.INSENSITIVE));
        this.ext = ext;
        this.desc = desc;
        this.shortDesc = shortDesc;
    }

    @Override
    public String getShortDescription() {
        return shortDesc;
    }

    @Override
    public String getDescription() {
        return desc;
    }

}
