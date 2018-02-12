package com.hashengineering.crypto;

import fr.cryptohash.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by KAALI for the nist5 algorithm
 */
public class Nist5 {

    private static final Logger log = LoggerFactory.getLogger(Nist5.class);
    private static boolean native_library_loaded = false;

    static {

        try {
            System.loadLibrary("nist5");
            native_library_loaded = true;
        }
        catch(UnsatisfiedLinkError x)
        {

        }
        catch(Exception e)
        {
            native_library_loaded = false;
        }
    }

    public static byte[] nist5Digest(byte[] input, int offset, int length)
    {
        byte [] buf = new byte[length];
        for(int i = 0; i < length; ++i)
        {
            buf[i] = input[offset + i];
        }
        return nist5Digest(buf);
    }

    public static byte[] nist5Digest(byte[] input) {
        //long start = System.currentTimeMillis();
        try {
            return native_library_loaded ? nist5_native(input) : nist5(input);
            /*long start = System.currentTimeMillis();
            byte [] result = x11_native(input);
            long end1 = System.currentTimeMillis();
            byte [] result2 = x11(input);
            long end2 = System.currentTimeMillis();
            log.info("x11: native {} / java {}", end1-start, end2-end1);
            return result;*/
        } catch (Exception e) {
            return null;
        }
        finally {
            //long time = System.currentTimeMillis()-start;
            //log.info("X11 Hash time: {} ms per block", time);
        }
    }

    static native byte [] nist5_native(byte [] input);


    static byte [] nist5(byte header[])
    {
        //Initialize
        Sha512Hash[] hash = new Sha512Hash[5];

        //Run the chain of algorithms
        BLAKE512 blake512 = new BLAKE512();
        hash[0] = new Sha512Hash(blake512.digest(header));

        //BMW512 bmw = new BMW512();
        //hash[1] = new Sha512Hash(bmw.digest(hash[0].getBytes()));

        Groestl512 groestl = new Groestl512();
        hash[1] = new Sha512Hash(groestl.digest(hash[0].getBytes()));

        //Skein512 skein = new Skein512();
        //hash[3] = new Sha512Hash(skein.digest(hash[2].getBytes()));

        JH512 jh = new JH512();
        hash[2] = new Sha512Hash(jh.digest(hash[1].getBytes()));

        Keccak512 keccak = new Keccak512();
        hash[3] = new Sha512Hash(keccak.digest(hash[2].getBytes()));
		
		Skein512 skein = new Skein512();
        hash[4] = new Sha512Hash(skein.digest(hash[3].getBytes()));

       /*  Luffa512 luffa = new Luffa512();
        hash[6] = new Sha512Hash(luffa.digest(hash[5].getBytes()));

        CubeHash512 cubehash = new CubeHash512();
        hash[7] = new Sha512Hash(cubehash.digest(hash[6].getBytes()));

        SHAvite512 shavite = new SHAvite512();
        hash[8] = new Sha512Hash(shavite.digest(hash[7].getBytes()));

        SIMD512 simd = new SIMD512();
        hash[9] = new Sha512Hash(simd.digest(hash[8].getBytes()));

        ECHO512 echo = new ECHO512();
        hash[10] = new Sha512Hash(echo.digest(hash[9].getBytes())); */

        return hash[4].trim256().getBytes();
    }
}
