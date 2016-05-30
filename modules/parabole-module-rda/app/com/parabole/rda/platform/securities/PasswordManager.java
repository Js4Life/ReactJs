// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// PasswordManager.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.securities;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.commons.lang3.Validate;
import play.Logger;

/**
 * Password Manager Utilities.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class PasswordManager {

    private static final int iterations = 10 * 1024;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    public static String generateHashPassword(final String password) {
        Validate.notBlank(password, "'password' cannot be null!");
        byte[] salt;
        try {
            salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
            return Base64.getEncoder().encodeToString(salt) + "$" + hash(password, salt);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            Logger.error("Hash function error", ex);
        }
        return null;
    }

    public static boolean check(final String password, final String stored) {
        Validate.notBlank(password, "'password' cannot be null!");
        Validate.notBlank(stored, "'stored' cannot be null!");
        final String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length == 2) {
            try {
                final String hashOfInput = hash(password, Base64.getDecoder().decode(saltAndPass[0]));
                return hashOfInput.equals(saltAndPass[1]);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
                Logger.error("Hash function error", ex);
            }
        }
        return false;
    }

    private static String hash(final String password, final byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Validate.notBlank(password, "'password' cannot be null!");
        Validate.notNull(salt, "'salt' cannot be null!");
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final SecretKey key = secretKeyFactory.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
