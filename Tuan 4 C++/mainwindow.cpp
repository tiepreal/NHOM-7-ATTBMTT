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
#include <QSignalBlocker>

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

    QGroupBox *leftBox =
        new QGroupBox("Bản rõ");

    QVBoxLayout *leftLayout =
        new QVBoxLayout;

    leftLayout->addWidget(plainText);
    leftBox->setLayout(leftLayout);

    QGroupBox *rightBox =
        new QGroupBox("Bản mã");

    QVBoxLayout *rightLayout =
        new QVBoxLayout;

    rightLayout->addWidget(cipherText);
    rightBox->setLayout(rightLayout);

    QHBoxLayout *textLayout =
        new QHBoxLayout;

    textLayout->addWidget(leftBox);
    textLayout->addWidget(rightBox);

    //---------------- BUTTONS ----------------

    QPushButton *btnOpen =
        new QPushButton("Mở File");

    QPushButton *btnEncrypt =
        new QPushButton("Mã hóa");

    QPushButton *btnDecrypt =
        new QPushButton("Giải mã");

    QPushButton *btnSaveKey =
        new QPushButton("Lưu khóa");
    QPushButton *btnSavePlain =
        new QPushButton("Lưu bản rõ");
    QPushButton *btnSaveCipher =
        new QPushButton("Lưu bản mã");

    QPushButton *btnClear =
        new QPushButton("Làm mới");

    QHBoxLayout *buttonLayout =
        new QHBoxLayout;


    buttonLayout->addWidget(btnOpen);
    buttonLayout->addWidget(btnEncrypt);
    buttonLayout->addWidget(btnDecrypt);
    buttonLayout->addWidget(btnSaveKey);
    buttonLayout->addWidget(btnSavePlain);
    buttonLayout->addWidget(btnSaveCipher);
    buttonLayout->addWidget(btnClear);

    btnOpen->setStyleSheet("background:#34495e;color:white;");
    btnEncrypt->setStyleSheet("background:#2ecc71;color:white;");
    btnDecrypt->setStyleSheet("background:#e67e22;color:white;");
    btnClear->setStyleSheet("background:#e74c3c;color:white;");
    btnSavePlain->setStyleSheet("background:#1abc9c;color:white;");
    btnSaveCipher->setStyleSheet("background:#8e44ad;color:white;");
    btnSaveKey->setStyleSheet("background:#f39c12;color:white;");

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

    connect(btnSaveKey, &QPushButton::clicked,
            this, &MainWindow::saveKey);

    connect(btnSavePlain, &QPushButton::clicked,
            this, &MainWindow::savePlain);

    connect(btnSaveCipher, &QPushButton::clicked,
            this, &MainWindow::saveCipher);

    connect(btnClear,&QPushButton::clicked,
            this,&MainWindow::clearAll);
    connect(keyEdit, &QLineEdit::textChanged,
            this, &MainWindow::onKeyEdited);

    connect(cipherText, &QTextEdit::textChanged,
            this, &MainWindow::onCipherEdited);
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
    QByteArray plainData = plain.toUtf8();

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
    cipherText->setText(cipherHex);

    QMessageBox::information(
        this,
        "Thành công",
        "Mã hóa AES thành công");

    keyChanged = false;
    cipherChanged = false;

}

void MainWindow::decryptText()
{
    QString errorMsg;

    if(keyChanged)
        errorMsg += "Khóa đã bị chỉnh sửa!\n";

    if(cipherChanged)
        errorMsg += "Bản mã đã bị chỉnh sửa!\n";

    if(!errorMsg.isEmpty())
    {
        QMessageBox::critical(this,
                              "Cảnh báo",
                              errorMsg);
        return;
    }

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
    QString fileName = QFileDialog::getOpenFileName(this, "Mở File");
    if(fileName.isEmpty()) return;

    QFile file(fileName);
    if(!file.open(QIODevice::ReadOnly | QIODevice::Text))
        return;

    QString content = file.readAll();
    file.close();

    QMessageBox msgBox;
    msgBox.setWindowTitle("Chọn loại dữ liệu");
    msgBox.setText("Bạn muốn đưa dữ liệu vào đâu?");

    QPushButton *yesBtn = msgBox.addButton("Bản rõ", QMessageBox::YesRole);
    QPushButton *noBtn  = msgBox.addButton("Bản mã", QMessageBox::NoRole);
    QPushButton *cancelBtn = msgBox.addButton("Khóa", QMessageBox::RejectRole);

    msgBox.exec();

    QAbstractButton *clicked = msgBox.clickedButton();

    if(clicked == yesBtn)
    {
        plainText->setText(content);
    }
    else if(clicked == noBtn)
    {
        QSignalBlocker blocker(cipherText);
        cipherText->setText(content);
        cipherChanged = false;
    }
    else if(clicked == cancelBtn)
    {
        QSignalBlocker blocker(keyEdit);
        keyEdit->setText(content);
        keyChanged = false;
    }
}

void MainWindow::saveKey()
{
    if(keyEdit->text().isEmpty())
    {
        QMessageBox::warning(this, "Lỗi", "Chưa có khóa");
        return;
    }
    QString fileName = QFileDialog::getSaveFileName(this, "Lưu khóa");
    if(fileName.isEmpty()) return;

    QFile file(fileName);
    if(file.open(QIODevice::WriteOnly | QIODevice::Text))
    {
        QTextStream out(&file);
        out << keyEdit->text();
        file.close();
    }
}

void MainWindow::savePlain()
{
    if(plainText->toPlainText().isEmpty())
    {
        QMessageBox::warning(this, "Lỗi", "Chưa có bản rõ");
        return;
    }
    QString fileName = QFileDialog::getSaveFileName(this, "Lưu bản rõ");
    if(fileName.isEmpty()) return;

    QFile file(fileName);
    if(file.open(QIODevice::WriteOnly | QIODevice::Text))
    {
        QTextStream out(&file);
        out << plainText->toPlainText();
        file.close();
    }
}

void MainWindow::saveCipher()
{
    if(cipherText->toPlainText().isEmpty())
    {
        QMessageBox::warning(this, "Lỗi", "Chưa có bản mã");
        return;
    }
    QString fileName = QFileDialog::getSaveFileName(this, "Lưu bản mã");
    if(fileName.isEmpty()) return;

    QFile file(fileName);
    if(file.open(QIODevice::WriteOnly | QIODevice::Text))
    {
        QTextStream out(&file);
        out << cipherText->toPlainText();
        file.close();
    }
}

void MainWindow::clearAll()
{
    plainText->clear();
    cipherText->clear();
    keyEdit->clear();
}
void MainWindow::onKeyEdited()
{
    keyChanged = true;
}

void MainWindow::onCipherEdited()
{
    cipherChanged = true;
}