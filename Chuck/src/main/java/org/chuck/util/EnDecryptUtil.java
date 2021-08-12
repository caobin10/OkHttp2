package org.chuck.util;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES比DES更安全,请优先采用AES
 * @author chuck
 *
 */
public class EnDecryptUtil {
	
	public static final String IV_PARAM="";
	
	private EnDecryptUtil() {
		
	}
    
	public static String MD5HexEncrypt(String plainText) {
		String text = null;
		try {
			text=MD5Util.encryptHex(plainText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}
	public static String MD5Base64Encrypt(String plainText) {
		String text = null;
		try {
			text=MD5Util.encryptBase64(plainText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}
	
	public static String AESEncrypt(String plainText,String key) throws Exception{
		return AESUtil.encrypt(plainText,key);
	}
	
	public static String AESDecrypt(String cipherText,String key) throws Exception{
		return AESUtil.decrypt(cipherText,key);
	}
	
	public static String DESEncrypt(String plainText, String key) throws Exception{
		return DESUtil.encrypt(plainText, key);
	}
	
	public static String DESDecrypt(String cipherText, String key) throws Exception{
		return DESUtil.decrypt(cipherText, key);
	}
	
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~RSA加密解密~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	
	
	/**
	 * 
	 * 将bytes转换成16进制的字符串
	 * @param bytes
	 * @return
	 */
	private static String toHexStr(byte[] bytes){
		StringBuilder sBuilder = new StringBuilder(bytes.length * 2);
		for (byte cByte: bytes) { 
            sBuilder.append(Character.forDigit(cByte >>> 4 & 0xf, 16))
            	    .append(Character.forDigit(cByte & 0xf, 16));  
//          sBuilder.append(Integer.toHexString(cByte & 0xff));
        }  
		return sBuilder.toString();
	}
	
	private static byte[] toBytes(String hexStr) {		 
        int len = hexStr.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++){
        	result[i] = Integer.valueOf(hexStr.substring(2 * i, 2 * i + 2),16).byteValue();
        }
        return result;
    }
	
	/**
	 * MD5加密类
	 * @author chuck
	 *
	 */
	public static class MD5Util{
		/**
		 * 标准的16进制MD5加密，如有需要请采用非标准加密或多次MD5加密
		 * @param plainText 要加密的明文
		 * @return
		 * @throws Exception 
		 */
		public static String encryptHex(String plainText) throws Exception{
			byte[] bytes=plainText.getBytes();
			MessageDigest instance=MessageDigest.getInstance("MD5");
			byte[] cipherBytes=instance.digest(bytes);				
			return toHexStr(cipherBytes);
		}
		public static String encryptBase64(String plainText) throws Exception{
			byte[] bytes=plainText.getBytes();
			MessageDigest instance=MessageDigest.getInstance("MD5");
			byte[] cipherBytes=instance.digest(bytes);			  
			return Base64.encodeToString(cipherBytes, Base64.DEFAULT);
		}
	}
	
	
	/**
	 * AES加密类
	 * <p>Java Cryptography Extension（JCE）AES支持CBC，CFB，ECB，OFB，PCBC五种模式；NoPadding，PKCS5Padding三种填充
	 * 其中NoPadding填充情况下，不支持CBC、ECB和PCBC三种模式。。
	 * </p>
	 * <p>不带模式和填充获取AES算法的时，默认使用ECB/PKCS5Padding，即等同AES/ECB/PKCS5Padding</p>
	 * @author chuck
	 *
	 */
	public static class AESUtil{
		public static String encrypt(String plainText,String key) throws Exception{
			byte[] keyBytes=getRawKey(key.getBytes());
			byte[] bytes=encrypt(keyBytes, plainText.getBytes());
			return toHexStr(bytes);
		}
		
		public static String decrypt(String cipherText,String key) throws Exception{
			byte[] keyBytes=getRawKey(key.getBytes());
			byte[] bytes=decrypt(keyBytes, toBytes(cipherText));
			return new String(bytes);
		}
		
		@SuppressLint("TrulyRandom") 
		public static byte[]  getRawKey(byte[] seed) throws Exception{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//			// 在4.2以上版本中，SecureRandom获取方式发生了改变
//			if (android.os.Build.VERSION.SDK_INT >= JELLY_BEAN_4_2) {
//			sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
//			} else {
//			sr = SecureRandom.getInstance("SHA1PRNG");
//			}
			keyGen.init(128, new SecureRandom(seed));	
			return keyGen.generateKey().getEncoded();
		}
		
		public static byte[] encrypt(byte[] keyBytes,byte[] plainBytes) throws Exception{
			SecretKeySpec secretKey=new SecretKeySpec(keyBytes, "AES");
			Cipher cipher=Cipher.getInstance("AES");
			IvParameterSpec ivParameterSpec=new IvParameterSpec(IV_PARAM.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivParameterSpec);
			return cipher.doFinal(plainBytes);
		}
		
		public static byte[] decrypt(byte[] keyBytes,byte[] cipherBytes) throws Exception{
			SecretKeySpec secretKey=new SecretKeySpec(keyBytes, "AES");
			Cipher cipher=Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(cipherBytes);
		}		
		
	}

	/**
	 * DES加密类
	 * <p>Java Cryptography Extension（JCE）DES支持CBC，CFB，ECB，OFB，PCBC五种模式；NoPadding，PKCS5Padding三种填充
	 * 其中NoPadding填充情况下，不支持CBC、ECB和PCBC三种模式。。密钥长度需是8的倍数
	 * </p>
	 * <p>DES/ECB/NoPadding 模式不支持向量</p>
	 * <p>不带模式和填充获取AES算法的时，默认使用ECB/PKCS5Padding，即等同DES/ECB/PKCS5Padding</p>
	 * @author chuck
	 *
	 */
	@Deprecated
	public static class DESUtil{
		public static String encrypt(String plainText,String key) throws Exception{
			DESKeySpec dks=new DESKeySpec(key.getBytes());
			SecretKeyFactory sKeyfactory=SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = sKeyfactory.generateSecret(dks);
			Cipher cipher=Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] bytes=cipher.doFinal(plainText.getBytes());
			return toHexStr(bytes);
		}
		public static String decrypt(String cipherText,String key) throws Exception{
			DESKeySpec dks=new DESKeySpec(key.getBytes());
			SecretKeyFactory sKeyfactory=SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = sKeyfactory.generateSecret(dks);
			Cipher cipher=Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] bytes=cipher.doFinal(toBytes(cipherText));
			return new String(bytes);
		}
	}
	
	
	/**
	 * RSA加密类
	 * <p>由于RSA安全性依赖于大数分解，故其效率相较低</p>
	 * @author chuck
	 *
	 */
	public static class RSAUtil{
		public static final String PUBLIC_KEY="";
		public static final String PRIVATE_KEY="";
		
		public static KeyPair generateKey() throws Exception{
			KeyPairGenerator generator=KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
			return generator.genKeyPair();
		}
		
		public static PublicKey getPublicKey() throws Exception{
			return generateKey().getPublic();
		}
		public static PrivateKey getPrivateKey() throws Exception{
			return generateKey().getPrivate();
		}
		
		public static String encrypt(String plainText) throws Exception{
			RSAPublicKey pubKey=(RSAPublicKey) getPublicKey();
			BigInteger exponent=pubKey.getPublicExponent();
			BigInteger modulus=pubKey.getModulus();
			BigInteger plainBi=new BigInteger(plainText.getBytes());
			BigInteger result=plainBi.modPow(exponent, modulus);
			return result.toString();
		}
		public static String decrypt(String cipherText) throws Exception{
			RSAPrivateKey priKey=(RSAPrivateKey) getPrivateKey();
			BigInteger exponent=priKey.getPrivateExponent();
			BigInteger modulus=priKey.getModulus();
			BigInteger cipherBi=new BigInteger(cipherText);
			BigInteger result=cipherBi.modPow(exponent, modulus);
			return result.toString();
		}
	}
	/**
	 * DSA加密类
	 * @author chuck
	 *
	 */
	public static class DSAUtil{
		public static PublicKey getPublicKey() throws Exception{
			return null;
		}
		public static PrivateKey getPrivateKey() throws Exception{
			return null;
		}
		
		public static String encrypt(String plainText) throws Exception{			
			return null;
		}
		public static String decrypt(String cipherText) throws Exception{
			return null;
		}
	}
	
	/**
	 * ECC加密类
	 * @author chuck
	 *
	 */
	public static class ECCUtil{
		public static KeyPair generateKey() throws Exception{
//			BigInteger affineX=new BigInteger("", 16);
//			BigInteger affineY=new BigInteger("", 16);
//			ECPoint generator=new ECPoint(affineX, affineY);
//			
//			EllipticCurve curve=new EllipticCurve(field, a, b, seed);
//			ECParameterSpec ecParameter=new ECParameterSpec(curve, generator, order, cofactor);
//			
//			ECPublicKey pubKey=new ECPublicKey() {
//				
//				@Override
//				public ECParameterSpec getParams() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//				
//				@Override
//				public String getFormat() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//				
//				@Override
//				public byte[] getEncoded() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//				
//				@Override
//				public String getAlgorithm() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//				
//				@Override
//				public ECPoint getW() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//			};
			return null;
		}
		
		public static PublicKey getPublicKey() throws Exception{
			return null;
		}
		public static PrivateKey getPrivateKey() throws Exception{
			return null;
		}
		
		public static String encrypt(String plainText) throws Exception{			
			return null;
		}
		public static String decrypt(String cipherText) throws Exception{
			return null;
		}
	}
	
}
