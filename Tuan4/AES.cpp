#include <iostream>
#include <vector>
#include <string>
#include <cstdio>

using namespace std;

// Ham thuc hien phep XOR giua hai byte
unsigned char XORByte(unsigned char a, unsigned char b)
{
    return a ^ b;
}

// Ham nhan voi x trong truong GF(2^8)
unsigned char xtime(unsigned char x)
{
    if (x & 0x80)
        return (x << 1) ^ 0x1B;
    else
        return x << 1;
}

// Ham nhan hai phan tu trong truong GF(2^8)
unsigned char GFMul(unsigned char a, unsigned char b)
{
    unsigned char result = 0;

    while (b)
    {
        if (b & 1)
            result ^= a;

        a = xtime(a);
        b >>= 1;
    }

    return result;
}

// Chuyen chuoi thanh mang byte
vector<unsigned char> StringToBytes(const string& str)
{
    vector<unsigned char> bytes;

    for (size_t i = 0; i < str.length(); i++)
    {
        bytes.push_back((unsigned char)str[i]);
    }

    return bytes;
}

// Hien thi du lieu dang Hex
void PrintHex(const vector<unsigned char>& data)
{
    for (size_t i = 0; i < data.size(); i++)
    {
        printf("%02X ", data[i]);
    }

    printf("\n");
}

int main()
{
    string text = "AES Example";

    vector<unsigned char> bytes = StringToBytes(text);

    cout << "Du lieu dang HEX: ";
    PrintHex(bytes);

    cout << "XOR(0x57, 0x83) = 0x"
         << hex
         << (int)XORByte(0x57, 0x83)
         << endl;

    cout << "GFMul(0x57, 0x13) = 0x"
         << hex
         << (int)GFMul(0x57, 0x13)
         << endl;

    return 0;
}
