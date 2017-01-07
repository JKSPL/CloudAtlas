package pl.edu.mimuw.cloudatlas;

import com.esotericsoftware.kryo.Kryo;
import pl.edu.mimuw.cloudatlas.interpreter.QueryResult;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.interpreter.query.Yylex;
import pl.edu.mimuw.cloudatlas.interpreter.query.parser;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by jks on 1/3/17.
 */
public class QuerySigner implements SigningInterface {
    HashMap<String, String> installedQueries = new HashMap<>();
    private final static String ENCRYPTION_ALGORITHM = "RSA";
    private final static int NUM_KEY_BITS = 1024;
    static KeyPair keyPair = null;
    static PrivateKey privateKey = null;
    static PublicKey publicKey = null;
    static Cipher signCipher;
    public static void main(String [] args){
        //TODO weryfikacja query
        String spvkey = "config.privatekey";
        String spukey = "config.publickey";
        File fv = new File(spvkey);
        File fu = new File(spukey);
        if(!fv.exists() || !fu.exists()){
            try {
                KeyPairGenerator keyGenerator = null;
                try {
                    keyGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return;
                }
                keyGenerator.initialize(NUM_KEY_BITS);
                keyPair = keyGenerator.generateKeyPair();
                privateKey = keyPair.getPrivate();
                publicKey = keyPair.getPublic();
                FileOutputStream fpv = new FileOutputStream(spvkey);
                FileOutputStream fpu = new FileOutputStream(spukey);
                fpv.write(privateKey.getEncoded());
                fpv.close();
                fpu.write(publicKey.getEncoded());
                fpu.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                byte[] bpv = Files.readAllBytes(Paths.get(spvkey));
                byte[] bpu = Files.readAllBytes(Paths.get(spukey));
                publicKey = keyFactory.generatePublic (new X509EncodedKeySpec(bpu));
                privateKey =  keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bpv));
                keyPair = new KeyPair(publicKey, privateKey);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            signCipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            signCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        QuerySigner object = new QuerySigner();
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            SigningInterface stub = (SigningInterface) UnicastRemoteObject.exportObject(object, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("QuerySigner", stub);
            System.out.println("QuerySigner bound");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    @Override
    synchronized public byte[] signInstallQuery(String query, String name) throws RemoteException {
        QueryInfo q = new QueryInfo(query, name);
        if(installedQueries.containsKey(name)){
            return null;
        }
        Yylex lex = new Yylex(new ByteArrayInputStream(query.getBytes()));
        try {
            new parser(lex).pProgram();
        } catch (Exception e) {
            return null;
        }
        //new parser(lex).pProgram();
        installedQueries.put(query, name);
        return getSign(q);
    }
    String DIGEST_ALGORITHM = "SHA-1";
    public byte [] getSign(QueryInfo q){
        Kryo k = new Kryo();
        byte[] bytes = q.serialize();
        try {
            MessageDigest digestGenerator =
                    MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] digest = digestGenerator.digest(bytes);
            return signCipher.doFinal(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    synchronized public byte[] signUninstallQuery(String name) throws RemoteException {
        if(!installedQueries.containsKey(name)){
            return null;
        }
        QueryInfo q = new QueryInfo(installedQueries.get(name), name);
        return getSign(q);
    }
}
