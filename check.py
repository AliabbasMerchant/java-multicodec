from multicodec import *
import varint

def get_bits(b):
    a = ""
    for i in range(1,9):
        if b&(2**(8-i)) != 0:
            a += "1"
        else:
            a += "0"
    return a

def get_str_bits(a):
    for t in a:
        print(get_bits(t), end=" ")
    print()

# get_str_bits(varint.encode(128))
something = b'EiC5TSe5k00'
get_str_bits(something)

lists = ["sha2-256", "blake2b-120"]
for codec in lists:
    print(codec)
    a = add_prefix(codec, something)
    get_str_bits(a)
