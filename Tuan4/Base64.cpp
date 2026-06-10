#include <iostream>
#include <string>
#include <vector>

using namespace std;

// Bang ky tu Base64
const string BASE64_CHARS =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    "abcdefghijklmnopqrstuvwxyz"
    "0123456789+/";

// Ham ma hoa Base64
string Base64Encode(const string& input)
{
    string output;
    int val = 0;
    int valb = -6;
    for (size_t i = 0; i < input.length(); i++)
    {
        unsigned char c = input[i];
        val = (val << 8) + c;
        valb += 8;

        while (valb >= 0)
        {
            output.push_back(
                BASE64_CHARS[(val >> valb) & 0x3F]);
            valb -= 6;
        }
    }
    if (valb > -6)
    {
        output.push_back(
            BASE64_CHARS[((val << 8) >> (valb + 8)) & 0x3F]);
    }
    while (output.size() % 4)
    {
        output.push_back('=');
    }
    return output;
}

// Ham giai ma Base64
string Base64Decode(const string& input)
{
    vector<int> table(256, -1);
    for (int i = 0; i < 64; i++)
    {
        table[(unsigned char)BASE64_CHARS[i]] = i;
    }
    string output;
    int val = 0;
    int valb = -8;
    for (size_t i = 0; i < input.length(); i++)
    {
        unsigned char c = input[i];
        if (c == '=')
            break;
        if (table[c] == -1)
            continue;
        val = (val << 6) + table[c];
        valb += 6;
        if (valb >= 0)
        {
            output.push_back(
                char((val >> valb) & 0xFF));
            valb -= 8;
        }
    }
    return output;
}

// Ham chinh
int main()
{
    string plaintext;

    cout << "Nhap chuoi: ";
    getline(cin, plaintext);

    string encoded = Base64Encode(plaintext);

    cout << "\nSau khi ma hoa Base64:\n";
    cout << encoded << endl;

    string decoded = Base64Decode(encoded);

    cout << "\nSau khi giai ma Base64:\n";
    cout << decoded << endl;

    return 0;
}
