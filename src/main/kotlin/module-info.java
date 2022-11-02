/*-
 * #%L
 * Smart Charge
 * %%
 * Copyright (C) 2016 - 2020 Frederik Kammel
 * %%
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
 * #L%
 */
module smartCharger {
    requires kotlin.stdlib;
    requires jcabi.xml;
    requires java.xml;
    requires org.apache.commons.lang3;
    requires java.desktop;
    requires java.logging;
    requires org.slf4j;
    requires javafx.graphics;
    requires com.github.oshi;
    requires jcommander;
    requires kotlinPreferences;
    requires okhttp3;
    requires javaAutoStart;
    requires javafx.fxml;
    requires unique4j;
    requires javafx.controls;
    requires kotlinx.coroutines.core.jvm;
    requires nsmenufx;
    requires org.jdom2;

    opens com.github.vatbub.smartcharge;
    opens com.github.vatbub.smartcharge.profiles;
}
