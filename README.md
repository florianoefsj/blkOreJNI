## blkOreJNI ##
A Java interface to the C implementation of Lewi &amp; Wu Order-Revealing Encryption (FastORE) (https://github.com/kevinlewi/fastore) through JNI, which is based on:

  * [Order-Revealing Encryption: New Constructions, Applications, and Lower Bounds](https://eprint.iacr.org/2016/612.pdf) (ore_blk.h)

Author: Edson Floriano, MSc student @ PPGI/CiC-UnB

Advisor: Eduardo Alchieri (CiC-UnB)

Co-Advisor: Diego F. Aranha (IC-UNICAMP)

Contact Edson for questions about the code: florianoefsj@gmail.com

## Prerequisites ##

make sure you have the following installed:

 * GMP 5 (Ex.: sudo apt-get install libgmp-dev)
 * OpenSSL (Ex.: sudo apt-get install openssl)
 * Clang (Ex.: sudo apt-get install clang)
 * JDK 8

Currently, the FastORE system requires a processor that supports the AES-NI instruction set.

## Instalation ##

Clone and compile the FastORE:

    git clone --recursive https://github.com/kevinlewi/fastore.git
    cd fastore
    make
 
Generate the libraries liboreblk.so e libcrypto.so (Find your native libcrypto.so location, in this case, /usr/lib/openssl-1.0/ and change to it in next command):

    clang -g -Wall -O3 -o libcrypto.so -march=native -lgmp -lssl -lcrypto -shared -L/usr/lib/openssl-1.0/ crypto.c
    clang -g -Wall -O3 -o liboreblk.so -march=native -lgmp -lssl -lcrypto -shared -L./ ore_blk.c 

May be nacessary to use -fPIC option in the above command to compile.

Get out of the folder "fastore", compile the JAVA program and generate the Header file:

    cd .. 
    javac blkOreJNI.java
    javah -jni blkOreJNI
    cp blkOreJNI.h fastore/blkOreJNI.h

Compile the C program:

    clang -g -Wl,-rpath,./fastore -O3 -o fastore/liboreblkc.so -march=native -lgmp -lssl -shared -I../jdk1.8.0_144/include/ -I../jdk1.8.0_144/include/linux/ -L./fastore -loreblk -lcrypto blkOreC.c

## Using ##

Run the java class giving the local of the fastore library:

    java -Djava.library.path=./fastore blkOreJNI [op] [args]

Options and arguments:

 - To encrypt the number "n" under the key "secret_key" and write the resulting binary into the file "file_out", use the option: "enc", with args: "secret_key n file_out"

e.g.

    java -Djava.library.path=./fastore blkOreJNI enc mysecretkey 15 a.ctxt

 - To compare two ciphertexts previously generated under the same key and stored at the files "file1_in" and "file2_in", use the option: "cmp" with the args: "file1_in file2_in"

e.g.

    java -Djava.library.path=./fastore blkOreJNI cmp a.ctxt b.ctxt

The cmp results will be:
 * -1, if the clear number releated with a.ctxt is smaller then the one releated with b.ctxt;
 * 0, if they are equals;
 * 1, otherwise.


