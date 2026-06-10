#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QTextEdit>
#include <QLineEdit>
#include <QPushButton>

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);

private slots:
    void generateKey();
    void encryptText();
    void decryptText();
    void openFile();
    void saveFile();
    void clearAll();

private:
    QTextEdit *plainText;
    QTextEdit *cipherText;
    QLineEdit *keyEdit;

    QString originalKey;
    QString originalCipher;
};

#endif