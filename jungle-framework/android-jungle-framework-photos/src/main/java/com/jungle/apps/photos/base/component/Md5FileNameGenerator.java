/**
 * Android photos application project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
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

package com.jungle.apps.photos.base.component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5FileNameGenerator {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 36;

    public String generate(String imageUri) {
        byte[] md5 = this.getMD5(imageUri.getBytes());
        BigInteger bi = (new BigInteger(md5)).abs();
        return bi.toString(36);
    }

    private byte[] getMD5(byte[] data) {
        byte[] hash = null;

        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(data);
            hash = e.digest();
        } catch (NoSuchAlgorithmException err) {
            err.printStackTrace();
        }

        return hash;
    }
}