/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long accountId;

    @Column(length = 32, nullable = false, unique = true)
    @Size(min = 8, max = 32)
    @NotNull
    private String username;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String password;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    private String salt;
    
    @Column(nullable = false)
    private Boolean enabled;

    public Account() {
        this.salt = generateRandomString(32);
        this.enabled = true;
    }

    public Account(String username, String password) {
        this();
        this.username = username;
        this.password = hashPassword(password);
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getSalt() {
        return salt;
    }

    public String generateNewSalt() {
        this.salt = generateRandomString(32);
        return this.salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }
    
    public Boolean testPassword(String rawPassword) {
        return this.password == hashPassword(rawPassword);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountId != null ? accountId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.accountId == null && other.accountId != null) || (this.accountId != null && !this.accountId.equals(other.accountId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Account[ accountId=" + accountId + " ]";
    }

    private String generateRandomString(int length) {
        String password = "";

        try {
            SecureRandom wheel = SecureRandom.getInstance("SHA1PRNG");

            char[] alphaNumberic = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

            for (int i = 0; i < length; i++) {
                int random = wheel.nextInt(alphaNumberic.length);
                password += alphaNumberic[random];
            }

            return password;
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("********** Exception: " + ex);
            return null;
        }
    }

    public String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        return byteArrayToHexString(doMD5Hashing(password + this.salt));
    }

    private String byteArrayToHexString(byte[] bytes) {
        int lo = 0;
        int hi = 0;
        String hexString = "";

        for (int i = 0; i < bytes.length; i++) {
            lo = bytes[i];
            lo = lo & 0xff;
            hi = lo >> 4;
            lo = lo & 0xf;

            hexString += numToString(hi);
            hexString += numToString(lo);
        }

        return hexString;
    }

    public byte[] doMD5Hashing(String stringToHash) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
            return md.digest(stringToHash.getBytes());
        } catch (Exception ex) {
            return null;
        }
    }

    private String numToString(int num) {
        switch (num) {
            case 0:
                return "0";
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "a";
            case 11:
                return "b";
            case 12:
                return "c";
            case 13:
                return "d";
            case 14:
                return "e";
            case 15:
                return "f";
            default:
                return "";
        }
    }
}
