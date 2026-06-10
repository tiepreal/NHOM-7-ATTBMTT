#include <iostream>
#include <fstream>
#include <string>
#include <ctime>
#include <cstdlib>

using namespace std;

// ==================== TAO KHOA TU DONG ====================
string createRandomKey()
{
    string chars =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        "abcdefghijklmnopqrstuvwxyz"
        "0123456789";

    string key = "";

    for(int i = 0; i < 16; i++)
    {
        key += chars[rand() % chars.length()];
    }

    return key;
}

// ==================== MA HOA ====================
string encryptText(string plaintext, string key)
{
    string ciphertext = "";

    for(int i = 0; i < plaintext.length(); i++)
    {
        ciphertext += plaintext[i] ^
                      key[i % key.length()];
    }

    return ciphertext;
}

// ==================== GIAI MA ====================
string decryptText(string ciphertext, string key)
{
    string plaintext = "";

    for(int i = 0; i < ciphertext.length(); i++)
    {
        plaintext += ciphertext[i] ^
                     key[i % key.length()];
    }

    return plaintext;
}

// ==================== LUU FILE ====================
void saveToFile(string filename,
                string content)
{
    ofstream file(filename.c_str());

    if(file.is_open())
    {
        file << content;
        file.close();

        cout << "\nDa luu file thanh cong!\n";
    }
    else
    {
        cout << "\nKhong the luu file!\n";
    }
}

// ==================== MENU ====================
void menu()
{
    cout << "\n======================================";
    cout << "\n CHUONG TRINH MO PHONG MA HOA AES";
    cout << "\n======================================";
    cout << "\n1. Tao khoa tu dong";
    cout << "\n2. Nhap khoa thu cong";
    cout << "\n3. Ma hoa van ban";
    cout << "\n4. Giai ma van ban";
    cout << "\n5. Luu ban ma vao file";
    cout << "\n0. Thoat";
    cout << "\n======================================";
    cout << "\nLua chon: ";
}

// ==================== MAIN ====================
int main()
{
    srand(time(NULL));

    string key = "";
    string plaintext = "";
    string ciphertext = "";

    int choice;

    do
    {
        menu();

        cin >> choice;
        cin.ignore();

        switch(choice)
        {
            case 1:
            {
                key = createRandomKey();

                cout << "\nKhoa AES duoc tao:\n";
                cout << key << endl;

                break;
            }

            case 2:
            {
                cout << "\nNhap khoa AES: ";
                getline(cin, key);

                cout << "\nKhoa vua nhap:\n";
                cout << key << endl;

                break;
            }

            case 3:
            {
                if(key.empty())
                {
                    cout << "\nVui long tao khoa truoc!\n";
                    break;
                }

                cout << "\nNhap ban ro: ";
                getline(cin, plaintext);

                ciphertext =
                    encryptText(
                        plaintext,
                        key
                    );

                cout << "\nBan ma:\n";
                cout << ciphertext << endl;

                break;
            }

            case 4:
            {
                if(key.empty())
                {
                    cout << "\nVui long tao khoa truoc!\n";
                    break;
                }

                cout << "\nNhap ban ma: ";
                getline(cin, ciphertext);

                plaintext =
                    decryptText(
                        ciphertext,
                        key
                    );

                cout << "\nBan ro:\n";
                cout << plaintext << endl;

                break;
            }

            case 5:
            {
                if(ciphertext.empty())
                {
                    cout << "\nChua co du lieu ma hoa!\n";
                    break;
                }

                saveToFile(
                    "AES_Result.txt",
                    ciphertext
                );

                break;
            }

            case 0:
            {
                cout << "\nKet thuc chuong trinh!\n";
                break;
            }

            default:
            {
                cout << "\nLua chon khong hop le!\n";
            }
        }

    }
    while(choice != 0);

    return 0;
}
