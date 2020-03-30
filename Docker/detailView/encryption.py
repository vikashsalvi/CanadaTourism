import string
special = string.punctuation + " "+"’"

class cloudCipher(object):
    def encrypt(self,plain_text):
        key = "HelloWorldWarZStarts19"

        key_list = []
        input_list = []
        cipher = ""

        for i in range(65, 91):
            input_list.append(chr(i))

        for i in range(97, 123):
            input_list.append(chr(i))

        for i in range(48, 58):
            input_list.append(chr(i))

        for i in key:
            if (i not in key_list):
                key_list.append(i)

        for i in range(65, 91):
            if (chr(i) not in key_list):
                key_list.append(chr(i))

        for i in range(97, 123):
            if (chr(i) not in key_list):
                key_list.append(chr(i))

        for i in range(48, 58):
            if (chr(i) not in key_list):
                key_list.append(chr(i))

        for i in plain_text:
            if (i in special):
                cipher += i
            else:
                for j in range(len(input_list)):
                    if (i == input_list[j]):
                        cipher += key_list[j]

        return cipher



    def decrypt(self,cipher_text):
        key = "HelloWorldWarZStarts19"

        key_list = []
        input_list = []
        plain = ""

        for i in range(65, 91):
            input_list.append(chr(i))

        for i in range(97, 123):
            input_list.append(chr(i))

        for i in range(48, 58):
            input_list.append(chr(i))

        for i in key:
            if (i not in key_list):
                key_list.append(i)

        for i in range(65, 91):
            if (chr(i) not in key_list):
                key_list.append(chr(i))

        for i in range(97, 123):
            if (chr(i) not in key_list):
                key_list.append(chr(i))

        for i in range(48, 58):
            if (chr(i) not in key_list):
                key_list.append(chr(i))

        for i in cipher_text:
            if (i in special):
                plain += i
            else:
                for j in range(len(key_list)):
                    if (i == key_list[j]):
                        plain += input_list[j]

        return plain


