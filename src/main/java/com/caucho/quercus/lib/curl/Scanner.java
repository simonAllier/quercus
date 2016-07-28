package com.caucho.quercus.lib.curl;

/**
 * User: Simon
 * Date: 26/07/16
 * Time: 14:50
 */

import com.caucho.util.CharBuffer;

/**
 * Represents a HTTP header field values scanner.
 */
class Scanner {
    String _header;
    int _position;
    int _length;

    CharBuffer _cb;

    Scanner(String header)
    {
        _header = header;
        _position = header.indexOf("Digest") + "Digest".length();
        _length = header.length();

        _cb = CharBuffer.allocate();
    }

    String readKey()
    {
        int ch = skipWhitespace();

        if (ch < 0)
            return null;

        if (ch == ',')
            ch = skipWhitespace();

        do {
            _cb.append((char)ch);
        } while ((ch = read()) != '=');

        // discard quote
        read();

        String key = _cb.toString();
        _cb.clear();

        return key;
    }

    String readValue()
    {
        int ch;
        while ((ch = read()) != '"') {
            _cb.append((char)ch);
        }

        String value = _cb.toString();
        _cb.clear();

        return value;
    }

    int skipWhitespace()
    {
        int ch;

        while ((ch = read()) >= 0) {
            if (ch != ' '
                    && ch != '\t'
                    && ch != '\r'
                    && ch != '\n'
                    && ch != '\f')
                break;
        }

        return ch;
    }

    int read()
    {
        if (_position >= _length)
            return -1;
        else
            return _header.charAt(_position++);
    }

    void close()
    {
        _cb.free();
    }
}