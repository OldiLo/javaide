/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.duy.ide.java.diagnostic.parser.aapt;

import com.android.annotations.NonNull;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SkippingWarning2Parser extends AbstractAaptOutputParser {

    /**
     * Error message emitted when aapt skips a file because for example it's name is invalid, such
     * as a layout file name which starts with _. <p/> This error message is used by AAPT in Tools
     * 20 and later.
     */
    private static final Pattern MSG_PATTERN = Pattern
            .compile("    \\(skipping .+ '(.+)' due to ANDROID_AAPT_IGNORE pattern '.+'\\)");

    @Override
    public boolean parse(@NonNull String line, @NonNull OutputLineReader reader, @NonNull List<Message> messages, @NonNull ILogger logger)
            throws ParsingFailedException {
        Matcher m = MSG_PATTERN.matcher(line);
        if (!m.matches()) {
            return false;
        }
        String sourcePath = m.group(1);
        if (sourcePath != null && (sourcePath.startsWith(".") || sourcePath.endsWith("~"))) {
            return true;
        }
        Message msg = createMessage(Message.Kind.WARNING, line, sourcePath,
                null, "", logger);
        messages.add(msg);
        return true;
    }
}
