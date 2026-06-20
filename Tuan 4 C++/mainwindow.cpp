#include "mainwindow.h"
#include <QLabel>
#include <QByteArray>
#include <QWidget>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QGroupBox>
#include <QFileDialog>
#include <QMessageBox>
#include <QRandomGenerator>
#include <QFile>
#include <QTextStream>
#include "qaesencryption.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    resize(900,600);
    setWindowTitle("🔐 HỆ THỐNG MÃ HÓA AES");
    QWidget *central = new QWidget;
    setCentralWidget(central);

    QVBoxLayout *mainLayout = new QVBoxLayout;

    //---------------- KEY ----------------

    QHBoxLayout *keyLayout = new QHBoxLayout;

    keyEdit = new QLineEdit;

    QPushButton *btnGen =
        new QPushButton("Sinh khóa");

    keyLayout->addWidget(
        new QLabel("Khóa AES:"));

    keyLayout->addWidget(keyEdit);

    keyLayout->addWidget(btnGen);

    //---------------- TEXT ----------------

    plainText = new QTextEdit;
    cipherText = new QTextEdit;

    base64Text = new QTextEdit;
    base64Text->setReadOnly(true);

    QGroupBox *leftBox =
        new QGroupBox("Bản rõ");

    QVBoxLayout *leftLayout =
        new QVBoxLayout;

    leftLayout->addWidget(plainText);
    leftBox->setLayout(leftLayout);

    QGroupBox *rightBox =
        new QGroupBox("Bản mã");

    QGroupBox *base64Box =
        new QGroupBox("Bản mã Base64");

    QVBoxLayout *base64Layout =
        new QVBoxLayout;

    base64Layout->addWidget(base64Text);
    base64Box->setLayout(base64Layout);

    QVBoxLayout *rightLayout =
        new QVBoxLayout;

    rightLayout->addWidget(cipherText);
    rightBox->setLayout(rightLayout);

    QHBoxLayout *textLayout =
        new QHBoxLayout;

    textLayout->addWidget(leftBox);
    textLayout->addWidget(rightBox);
    textLayout->addWidget(base64Box);

    //---------------- BUTTONS ----------------

    QPushButton *btnOpen =
        new QPushButton("Mở File");

    QPushButton *btnEncrypt =
        new QPushButton("Mã hóa");

    QPushButton *btnDecrypt =
        new QPushButton("Giải mã");

    QPushButton *btnSave =
        new QPushButton("Lưu");

    QPushButton *btnClear =
        new QPushButton("Làm mới");

    QHBoxLayout *buttonLayout =
        new QHBoxLayout;

    buttonLayout->addWidget(btnOpen);
    buttonLayout->addWidget(btnEncrypt);
    buttonLayout->addWidget(btnDecrypt);
    buttonLayout->addWidget(btnSave);
    buttonLayout->addWidget(btnClear);

    btnOpen->setStyleSheet("background:#34495e;color:white;");
    btnEncrypt->setStyleSheet("background:#2ecc71;color:white;");
    btnDecrypt->setStyleSheet("background:#e67e22;color:white;");
    btnSave->setStyleSheet("background:#9b59b6;color:white;");
    btnClear->setStyleSheet("background:#e74c3c;color:white;");

    //---------------- MAIN ----------------

    mainLayout->addLayout(keyLayout);
    mainLayout->addLayout(textLayout);
    mainLayout->addLayout(buttonLayout);

    central->setLayout(mainLayout);

    connect(btnGen,&QPushButton::clicked,
            this,&MainWindow::generateKey);

    connect(btnEncrypt,&QPushButton::clicked,
            this,&MainWindow::encryptText);

    connect(btnDecrypt,&QPushButton::clicked,
            this,&MainWindow::decryptText);

    connect(btnOpen,&QPushButton::clicked,
            this,&MainWindow::openFile);

    connect(btnSave,&QPushButton::clicked,
            this,&MainWindow::saveFile);

    connect(btnClear,&QPushButton::clicked,
            this,&MainWindow::clearAll);
}

void MainWindow::generateKey()
{
    QString chars =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        "abcdefghijklmnopqrstuvwxyz"
        "0123456789";

    QString key;

    for(int i=0;i<16;i++)
    {
        int index =
            QRandomGenerator::global()
                ->bounded(chars.length());

        key += chars[index];
    }

    keyEdit->setText(key);
}

void MainWindow::encryptText()
{
    QString key = keyEdit->text();

    if(key.length() != 16)
    {
        QMessageBox::warning(
            this,
            "Lỗi",
            "Khóa AES-128 phải có đúng 16 ký tự");
        return;
    }

    QString plain = plainText->toPlainText();

    if(plain.isEmpty())
    {
        QMessageBox::warning(
            this,
            "Lỗi",
            "Vui lòng nhập bản rõ");
        return;
    }

    bool ok;

    QByteArray cipherData =
        QAESEncryption::Crypt(
            QAESEncryption::AES_128,
            QAESEncryption::ECB,
            plain.toUtf8(),
            key.toUtf8(),
            QByteArray(),
            QAESEncryption::PKCS7,
            &ok);

    if(!ok)
    {
        QMessageBox::critical(
            this,
            "Lỗi",
            "Mã hóa thất bại");
        return;
    }

    QString cipherHex =
        cipherData.toHex();

    QString cipherBase64 =
        cipherData.toBase64();

    cipherText->setText(cipherHex);

    base64Text->setText(cipherBase64);

    QMessageBox::information(
        this,
        "Thành công",
        "Mã hóa AES thành công");
}

void MainWindow::decryptText()
{
    QString key = keyEdit->text();
    QString cipher = cipherText->toPlainText();

    if(key.length() != 16)
    {
        QMessageBox::warning(
            this,
            "Lỗi",
            "Khóa AES-128 phải có đúng 16 ký tự");
        return;
    }

    bool ok;

    QByteArray decrypted =
        QAESEncryption::Decrypt(
            QAESEncryption::AES_128,
            QAESEncryption::ECB,
            QByteArray::fromHex(cipher.toUtf8()),
            key.toUtf8(),
            QByteArray(),
            QAESEncryption::PKCS7,
            &ok);

    if(!ok)
    {
        QMessageBox::critical(
            this,
            "Lỗi",
            "Giải mã thất bại");
        return;
    }

    plainText->setText(
        QString::fromUtf8(decrypted));

    QMessageBox::information(
        this,
        "Thành công",
        "Giải mã AES thành công");

}
void MainWindow::openFile()
{
    QString fileName =
        QFileDialog::getOpenFileName(
            this,
            "Mở File");

    if(fileName.isEmpty())
        return;

    QFile file(fileName);

    if(file.open(QIODevice::ReadOnly))
    {
        QTextStream in(&file);

        plainText->setText(
            in.readAll());

        file.close();
    }
}

void MainWindow::saveFile()
{
    QString fileName =
        QFileDialog::getSaveFileName(
            this,
            "Lưu File");

    if(fileName.isEmpty())
        return;

    QFile file(fileName);

    if(file.open(QIODevice::WriteOnly))
    {
        QTextStream out(&file);

        out << cipherText->toPlainText();

        file.close();

        QMessageBox::information(
            this,
            "Lưu",
            "Lưu file thành công");
    }
}

void MainWindow::clearAll()
{
    plainText->clear();
    cipherText->clear();
    keyEdit->clear();
}