package org.femtoframework.lang;

import org.femtoframework.io.ByteData;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.regex.Pattern;

public class OctetString
    implements Serializable, Comparable, CharSequence, ByteData {
    /**
     * The value is used for character storage.
     */
    private byte value[];

    /**
     * The offset is the first index of the storage that is used.
     */
    private int offset;

    /**
     * The count is the number of characters in the String.
     */
    private int count;

    /**
     * Cache the hash code for the string
     */
    private int hash = 0;

    static final byte[] BYTE_TRUE
        = new byte[]{'t', 'r', 'u', 'e'};
    static final byte[] BYTE_FALSE
        = new byte[]{'f', 'a', 'l', 's', 'e'};
    static final byte[] BYTE_NULL
        = new byte[]{'n', 'u', 'l', 'l'};

    public static final OctetString TRUE = new OctetString(BYTE_TRUE);
    public static final OctetString FALSE = new OctetString(BYTE_FALSE);
    public static final OctetString NULL = new OctetString(BYTE_NULL);

    /**
     * Initializes a newly created <code>String</code> object so that it
     * represents an empty character sequence.  Note that use of this
     * constructor is unnecessary since Strings are immutable.
     */
    public OctetString() {
        value = new byte[0];
    }

    /**
     * Initializes a newly created <code>String</code> object so that it
     * represents the same sequence of characters as the argument; in other
     * words, the newly created string is a copy of the argument string. Unless
     * an explicit copy of <code>original</code> is needed, use of this
     * constructor is unnecessary since Strings are immutable.
     *
     * @param original a <code>String</code>.
     */
    public OctetString(OctetString original) {
        this.count = original.count;
        if (original.value.length > this.count) {
            // The array representing the String is bigger than the new
            // String itself.  Perhaps this constructor is being called
            // in order to trim the baggage, so make a copy of the array.
            this.value = new byte[this.count];
            System.arraycopy(original.value, original.offset,
                this.value, 0, this.count);
        }
        else {
            // The array representing the String is the same
            // size as the String, so no point in making a copy.
            this.value = original.value;
        }
    }

    /**
     * 拷贝一个String对象来构建一个新OctetString对象
     *
     * @param str
     */
    public OctetString(String str) {
        this.value = str.getBytes();
        this.offset = 0;
        this.count = value.length;
    }

    /**
     * Allocates a new <code>String</code> so that it represents the
     * sequence of characters currently contained in the character array
     * argument. The contents of the character array are copied; subsequent
     * modification of the character array does not affect the newly created
     * string.
     *
     * @param value the initial value of the string.
     */
    public OctetString(byte value[]) {
        this.count = value.length;
        this.value = new byte[count];
        System.arraycopy(value, 0, this.value, 0, count);
    }

    /**
     * Allocates a new <code>String</code> that contains characters from
     * a subarray of the character array argument. The <code>offset</code>
     * argument is the index of the first character of the subarray and
     * the <code>count</code> argument specifies the length of the
     * subarray. The contents of the subarray are copied; subsequent
     * modification of the character array does not affect the newly
     * created string.
     *
     * @param value  array that is the source of characters.
     * @param offset the initial offset.
     * @param count  the length.
     * @throws IndexOutOfBoundsException if the <code>offset</code>
     *                                   and <code>count</code> arguments index characters outside
     *                                   the bounds of the <code>value</code> array.
     */
    public OctetString(byte value[], int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count < 0) {
            throw new StringIndexOutOfBoundsException(count);
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > value.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }

        this.value = new byte[count];
        this.count = count;
        System.arraycopy(value, offset, this.value, 0, count);
    }

//    /* Common private utility method used to bounds check the byte array
//     * and requested offset & length values used by the String(byte[],..)
//     * constructors.
//     */
//    private static void checkBounds(byte[] bytes, int offset, int length)
//    {
//        if (length < 0) {
//            throw new StringIndexOutOfBoundsException(length);
//        }
//        if (offset < 0) {
//            throw new StringIndexOutOfBoundsException(offset);
//        }
//        if (offset > bytes.length - length) {
//            throw new StringIndexOutOfBoundsException(offset + length);
//        }
//    }

    /**
     * Allocates a new string that contains the sequence of characters
     * currently contained in the string buffer argument. The contents of
     * the string buffer are copied; subsequent modification of the string
     * buffer does not affect the newly created string.
     *
     * @param buffer a <code>StringBuffer</code>.
     */
    public OctetString(OctetBuffer buffer) {
        buffer.setShared();
        this.value = buffer.getValue();
        this.offset = 0;
        this.count = buffer.length();
    }

    // Package private constructor which shares value array for speed.
    OctetString(int offset, int count, byte value[]) {
        this.value = value;
        this.offset = offset;
        this.count = count;
    }

    /**
     * Returns the length of this string.
     * The length is equal to the number of 16-bit
     * Unicode characters in the string.
     *
     * @return the length of the sequence of characters represented by this
     *         object.
     */
    public int length() {
        return count;
    }

    /**
     * Returns the character at the specified index.  An index ranges from zero
     * to <tt>length() - 1</tt>.  The first character of the sequence is at
     * index zero, the next at index one, and so on, as for array
     * indexing. </p>
     *
     * @param index the index of the character to be returned
     * @return the specified character
     * @throws IndexOutOfBoundsException if the <tt>index</tt> argument is negative or not less than
     *                                   <tt>length()</tt>
     */
    public char charAt(int index) {
        return (char)byteAt(index);
    }

    /**
     * Returns the character at the specified index. An index ranges
     * from <code>0</code> to <code>length() - 1</code>. The first character
     * of the sequence is at index <code>0</code>, the next at index
     * <code>1</code>, and so on, as for array indexing.
     *
     * @param index the index of the character.
     * @return the character at the specified index of this string.
     *         The first character is at index <code>0</code>.
     * @throws IndexOutOfBoundsException if the <code>index</code>
     *                                   argument is negative or not less than the length of this
     *                                   string.
     */
    public byte byteAt(int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index + offset];
    }

    /**
     * Copies characters from this string into the destination character
     * array.
     * <p/>
     * The first character to be copied is at index <code>srcBegin</code>;
     * the last character to be copied is at index <code>srcEnd-1</code>
     * (thus the total number of characters to be copied is
     * <code>srcEnd-srcBegin</code>). The characters are copied into the
     * subarray of <code>dst</code> starting at index <code>dstBegin</code>
     * and ending at index:
     * <p><blockquote><pre>
     *     dstbegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @param srcBegin index of the first character in the string
     *                 to copy.
     * @param srcEnd   index after the last character in the string
     *                 to copy.
     * @param dst      the destination array.
     * @param dstBegin the start offset in the destination array.
     * @throws IndexOutOfBoundsException If any of the following
     *                                   is true:
     *                                   <ul><li><code>srcBegin</code> is negative.
     *                                   <li><code>srcBegin</code> is greater than <code>srcEnd</code>
     *                                   <li><code>srcEnd</code> is greater than the length of this
     *                                   string
     *                                   <li><code>dstBegin</code> is negative
     *                                   <li><code>dstBegin+(srcEnd-srcBegin)</code> is larger than
     *                                   <code>dst.length</code></ul>
     */
    public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if (srcEnd > count) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        System.arraycopy(value, offset + srcBegin, dst, dstBegin,
            srcEnd - srcBegin);
    }

    /**
     * Compares this string to the specified object.
     * The result is <code>true</code> if and only if the argument is not
     * <code>null</code> and is a <code>String</code> object that represents
     * the same sequence of characters as this object.
     *
     * @param anObject the object to compare this <code>String</code>
     *                 against.
     * @return <code>true</code> if the <code>String </code>are equal;
     *         <code>false</code> otherwise.
     * @see OctetString#compareTo(OctetString)
     * @see OctetString#equalsIgnoreCase(OctetString)
     */
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof OctetString) {
            OctetString anotherString = (OctetString)anObject;
            int n = count;
            if (n == anotherString.count) {
                byte v1[] = value;
                byte v2[] = anotherString.value;
                int i = offset;
                int j = anotherString.offset;
                while (n-- != 0) {
                    if (v1[i++] != v2[j++]) {
                        return false;
                    }
                }
                return true;
            }
        }
        else if (anObject instanceof String) {
            String tmpString = new String(value);
            return anObject.equals(tmpString);
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if and only if this <tt>String</tt> represents
     * the same sequence of characters as the specified <tt>StringBuffer</tt>.
     *
     * @param sb the <tt>StringBuffer</tt> to compare to.
     * @return <tt>true</tt> if and only if this <tt>String</tt> represents
     *         the same sequence of characters as the specified
     *         <tt>StringBuffer</tt>, otherwise <tt>false</tt>.
     * @since 1.4
     */
    public boolean contentEquals(OctetBuffer sb) {
        if (count != sb.length()) {
            return false;
        }
        byte v1[] = value;
        byte v2[] = sb.getValue();
        int i = offset;
        int j = 0;
        int n = count;
        while (n-- != 0) {
            if (v1[i++] != v2[j++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this <code>String</code> to another <code>String</code>,
     * ignoring case considerations.  Two strings are considered equal
     * ignoring case if they are of the same length, and corresponding
     * characters in the two strings are equal ignoring case.
     * <p/>
     * Two characters <code>c1</code> and <code>c2</code> are considered
     * the same, ignoring case if at least one of the following is true:
     * <ul><li>The two characters are the same (as compared by the
     * <code>==</code> operator).
     * <li>Applying the method {@link Character#toUpperCase(char)}
     * to each character produces the same result.
     * <li>Applying the method {@link Character#toLowerCase(char)}
     * to each character produces the same result.</ul>
     *
     * @param anotherString the <code>String</code> to compare this
     *                      <code>String</code> against.
     * @return <code>true</code> if the argument is not <code>null</code>
     *         and the <code>String</code>s are equal,
     *         ignoring case; <code>false</code> otherwise.
     * @see #equals(Object)
     * @see Character#toLowerCase(char)
     * @see Character#toUpperCase(char)
     */
    public boolean equalsIgnoreCase(OctetString anotherString) {
        return (this == anotherString) || (anotherString != null) && (anotherString.count == count) &&
                                          regionMatches(true, 0, anotherString, 0, count);
    }

    /**
     * Compares two strings lexicographically.
     * The comparison is based on the Unicode value of each character in
     * the strings. The character sequence represented by this
     * <code>String</code> object is compared lexicographically to the
     * character sequence represented by the argument string. The result is
     * a negative integer if this <code>String</code> object
     * lexicographically precedes the argument string. The result is a
     * positive integer if this <code>String</code> object lexicographically
     * follows the argument string. The result is zero if the strings
     * are equal; <code>compareTo</code> returns <code>0</code> exactly when
     * the {@link #equals(Object)} method would return <code>true</code>.
     * <p/>
     * This is the definition of lexicographic ordering. If two strings are
     * different, then either they have different characters at some index
     * that is a valid index for both strings, or their lengths are different,
     * or both. If they have different characters at one or more index
     * positions, let <i>k</i> be the smallest such index; then the string
     * whose character at position <i>k</i> has the smaller value, as
     * determined by using the &lt; operator, lexicographically precedes the
     * other string. In this case, <code>compareTo</code> returns the
     * difference of the two character values at position <code>k</code> in
     * the two string -- that is, the value:
     * <blockquote><pre>
     * this.charAt(k)-anotherString.charAt(k)
     * </pre></blockquote>
     * If there is no index position at which they differ, then the shorter
     * string lexicographically precedes the longer string. In this case,
     * <code>compareTo</code> returns the difference of the lengths of the
     * strings -- that is, the value:
     * <blockquote><pre>
     * this.length()-anotherString.length()
     * </pre></blockquote>
     *
     * @param anotherString the <code>String</code> to be compared.
     * @return the value <code>0</code> if the argument string is equal to
     *         this string; a value less than <code>0</code> if this string
     *         is lexicographically less than the string argument; and a
     *         value greater than <code>0</code> if this string is
     *         lexicographically greater than the string argument.
     */
    public int compareTo(OctetString anotherString) {
        int len1 = count;
        int len2 = anotherString.count;
        int n = Math.min(len1, len2);
        byte v1[] = value;
        byte v2[] = anotherString.value;
        int i = offset;
        int j = anotherString.offset;

        if (i == j) {
            int k = i;
            int lim = n + i;
            while (k < lim) {
                byte c1 = v1[k];
                byte c2 = v2[k];
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
        }
        else {
            while (n-- != 0) {
                byte c1 = v1[i++];
                byte c2 = v2[j++];
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
        }
        return len1 - len2;
    }

    /**
     * Compares this String to another Object.  If the Object is a String,
     * this function behaves like <code>compareTo(String)</code>.  Otherwise,
     * it throws a <code>ClassCastException</code> (as Strings are comparable
     * only to other Strings).
     *
     * @param o the <code>Object</code> to be compared.
     * @return the value <code>0</code> if the argument is a string
     *         lexicographically equal to this string; a value less than
     *         <code>0</code> if the argument is a string lexicographically
     *         greater than this string; and a value greater than
     *         <code>0</code> if the argument is a string lexicographically
     *         less than this string.
     * @throws ClassCastException if the argument is not a
     *                            <code>String</code>.
     * @see Comparable
     * @since 1.2
     */
    public int compareTo(Object o) {
        return compareTo((OctetString)o);
    }

    /**
     * A Comparator that orders <code>String</code> objects as by
     * <code>compareToIgnoreCase</code>. This comparator is serializable.
     * <p/>
     * Note that this Comparator does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>Collators</em> to allow
     * locale-sensitive ordering.
     *
     * @since 1.2
     */
    public static final Comparator<OctetString> CASE_INSENSITIVE_ORDER
        = new OctetString.CaseInsensitiveComparator();

    private static class CaseInsensitiveComparator
        implements Comparator<OctetString>, Serializable {
        // use serialVersionUID from JDK 1.2.2 for interoperability
        private static final long serialVersionUID = 8575799808933029326L;

        public int compare(OctetString s1, OctetString s2) {
            int n1 = s1.length(), n2 = s2.length();
            for (int i1 = 0, i2 = 0; i1 < n1 && i2 < n2; i1++, i2++) {
                char c1 = s1.charAt(i1);
                char c2 = s2.charAt(i2);
                if (c1 != c2) {
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    if (c1 != c2) {
                        c1 = Character.toLowerCase(c1);
                        c2 = Character.toLowerCase(c2);
                        if (c1 != c2) {
                            return c1 - c2;
                        }
                    }
                }
            }
            return n1 - n2;
        }
    }

    /**
     * Compares two strings lexicographically, ignoring case
     * differences. This method returns an integer whose sign is that of
     * calling <code>compareTo</code> with normalized versions of the strings
     * where case differences have been eliminated by calling
     * <code>Character.toLowerCase(Character.toUpperCase(character))</code> on
     * each character.
     * <p/>
     * Note that this method does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>collators</em> to allow
     * locale-sensitive ordering.
     *
     * @param str the <code>String</code> to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         the specified String is greater than, equal to, or less
     *         than this String, ignoring case considerations.
     * @since 1.2
     */
    public int compareToIgnoreCase(OctetString str) {
        return CASE_INSENSITIVE_ORDER.compare(this, str);
    }

    /**
     * Tests if two string regions are equal.
     * <p/>
     * A substring of this <tt>String</tt> object is compared to a substring
     * of the argument other. The result is true if these substrings
     * represent identical character sequences. The substring of this
     * <tt>String</tt> object to be compared begins at index <tt>toffset</tt>
     * and has length <tt>len</tt>. The substring of other to be compared
     * begins at index <tt>ooffset</tt> and has length <tt>len</tt>. The
     * result is <tt>false</tt> if and only if at least one of the following
     * is true:
     * <ul><li><tt>toffset</tt> is negative.
     * <li><tt>ooffset</tt> is negative.
     * <li><tt>toffset+len</tt> is greater than the length of this
     * <tt>String</tt> object.
     * <li><tt>ooffset+len</tt> is greater than the length of the other
     * argument.
     * <li>There is some nonnegative integer <i>k</i> less than <tt>len</tt>
     * such that:
     * <tt>this.charAt(toffset+<i>k</i>)&nbsp;!=&nbsp;other.charAt(ooffset+<i>k</i>)</tt>
     * </ul>
     *
     * @param toffset the starting offset of the subregion in this string.
     * @param other   the string argument.
     * @param ooffset the starting offset of the subregion in the string
     *                argument.
     * @param len     the number of characters to compare.
     * @return <code>true</code> if the specified subregion of this string
     *         exactly matches the specified subregion of the string argument;
     *         <code>false</code> otherwise.
     */
    public boolean regionMatches(int toffset, OctetString other, int ooffset,
                                 int len) {
        byte ta[] = value;
        int to = offset + toffset;
        byte pa[] = other.value;
        int po = other.offset + ooffset;
        // Note: toffset, ooffset, or len might be near -1>>>1.
        if ((ooffset < 0) || (toffset < 0) || (toffset > (long)count - len)
            || (ooffset > (long)other.count - len)) {
            return false;
        }
        while (len-- > 0) {
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if two string regions are equal.
     * <p/>
     * A substring of this <tt>String</tt> object is compared to a substring
     * of the argument <tt>other</tt>. The result is <tt>true</tt> if these
     * substrings represent character sequences that are the same, ignoring
     * case if and only if <tt>ignoreCase</tt> is true. The substring of
     * this <tt>String</tt> object to be compared begins at index
     * <tt>toffset</tt> and has length <tt>len</tt>. The substring of
     * <tt>other</tt> to be compared begins at index <tt>ooffset</tt> and
     * has length <tt>len</tt>. The result is <tt>false</tt> if and only if
     * at least one of the following is true:
     * <ul><li><tt>toffset</tt> is negative.
     * <li><tt>ooffset</tt> is negative.
     * <li><tt>toffset+len</tt> is greater than the length of this
     * <tt>String</tt> object.
     * <li><tt>ooffset+len</tt> is greater than the length of the other
     * argument.
     * <li><tt>ignoreCase</tt> is <tt>false</tt> and there is some nonnegative
     * integer <i>k</i> less than <tt>len</tt> such that:
     * <blockquote><pre>
     * this.charAt(toffset+k) != other.charAt(ooffset+k)
     * </pre></blockquote>
     * <li><tt>ignoreCase</tt> is <tt>true</tt> and there is some nonnegative
     * integer <i>k</i> less than <tt>len</tt> such that:
     * <blockquote><pre>
     * Character.toLowerCase(this.charAt(toffset+k)) !=
     * Character.toLowerCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * and:
     * <blockquote><pre>
     * Character.toUpperCase(this.charAt(toffset+k)) !=
     *         Character.toUpperCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * </ul>
     *
     * @param ignoreCase if <code>true</code>, ignore case when comparing
     *                   characters.
     * @param toffset    the starting offset of the subregion in this
     *                   string.
     * @param other      the string argument.
     * @param ooffset    the starting offset of the subregion in the string
     *                   argument.
     * @param len        the number of characters to compare.
     * @return <code>true</code> if the specified subregion of this string
     *         matches the specified subregion of the string argument;
     *         <code>false</code> otherwise. Whether the matching is exact
     *         or case insensitive depends on the <code>ignoreCase</code>
     *         argument.
     */
    public boolean regionMatches(boolean ignoreCase, int toffset,
                                 OctetString other, int ooffset, int len) {
        byte ta[] = value;
        int to = offset + toffset;
        byte pa[] = other.value;
        int po = other.offset + ooffset;
        // Note: toffset, ooffset, or len might be near -1>>>1.
        if ((ooffset < 0) || (toffset < 0) || (toffset > (long)count - len) ||
            (ooffset > (long)other.count - len)) {
            return false;
        }
        while (len-- > 0) {
            byte c1 = ta[to++];
            byte c2 = pa[po++];
            if (c1 == c2) {
                continue;
            }
            if (ignoreCase) {
                // If characters don't match but case may be ignored,
                // try converting both characters to uppercase.
                // If the results match, then the comparison scan should
                // continue.
                byte u1 = Octet.toUpperCase(c1);
                byte u2 = Octet.toUpperCase(c2);
                if (u1 == u2) {
                    continue;
                }
                // Unfortunately, conversion to uppercase does not work properly
                // for the Georgian alphabet, which has strange rules about case
                // conversion.  So we need to make one last check before
                // exiting.
                if (Octet.toLowerCase(u1) == Octet.toLowerCase(u2)) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Tests if this string starts with the specified prefix beginning
     * a specified index.
     *
     * @param prefix  the prefix.
     * @param toffset where to begin looking in the string.
     * @return <code>true</code> if the character sequence represented by the
     *         argument is a prefix of the substring of this object starting
     *         at index <code>toffset</code>; <code>false</code> otherwise.
     *         The result is <code>false</code> if <code>toffset</code> is
     *         negative or greater than the length of this
     *         <code>String</code> object; otherwise the result is the same
     *         as the result of the expression
     *         <pre>
     *                                                                                                                                  this.subString(toffset).startsWith(prefix)
     *                                                                                                                                  </pre>
     */
    public boolean startsWith(OctetString prefix, int toffset) {
        byte ta[] = value;
        int to = offset + toffset;
        byte pa[] = prefix.value;
        int po = prefix.offset;
        int pc = prefix.count;
        // Note: toffset might be near -1>>>1.
        if ((toffset < 0) || (toffset > count - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if this string starts with the specified prefix.
     *
     * @param prefix the prefix.
     * @return <code>true</code> if the character sequence represented by the
     *         argument is a prefix of the character sequence represented by
     *         this string; <code>false</code> otherwise.
     *         Note also that <code>true</code> will be returned if the
     *         argument is an empty string or is equal to this
     *         <code>String</code> object as determined by the
     *         {@link #equals(Object)} method.
     * @since 1. 0
     */
    public boolean startsWith(OctetString prefix) {
        return startsWith(prefix, 0);
    }

    /**
     * Tests if this string ends with the specified suffix.
     *
     * @param suffix the suffix.
     * @return <code>true</code> if the character sequence represented by the
     *         argument is a suffix of the character sequence represented by
     *         this object; <code>false</code> otherwise. Note that the
     *         result will be <code>true</code> if the argument is the
     *         empty string or is equal to this <code>String</code> object
     *         as determined by the {@link #equals(Object)} method.
     */
    public boolean endsWith(OctetString suffix) {
        return startsWith(suffix, count - suffix.count);
    }

    /**
     * Returns a hash code for this string. The hash code for a
     * <code>String</code> object is computed as
     * <blockquote><pre>
     * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
     * </pre></blockquote>
     * using <code>int</code> arithmetic, where <code>s[i]</code> is the
     * <i>i</i>th character of the string, <code>n</code> is the length of
     * the string, and <code>^</code> indicates exponentiation.
     * (The hash value of the empty string is zero.)
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            int off = offset;
            byte val[] = value;
            int len = count;

            for (int i = 0; i < len; i++) {
                h = 31 * h + val[off++];
            }
            hash = h;
        }
        return h;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character. If a character with value <code>ch</code> occurs
     * in the character sequence represented by this <code>String</code>
     * object, then the index of the first such occurrence is returned --
     * that is, the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is <code>true</code>. If no such character occurs in this string,
     * then <code>-1</code> is returned.
     *
     * @param ch a character.
     * @return the index of the first occurrence of the character in the
     *         character sequence represented by this object, or
     *         <code>-1</code> if the character does not occur.
     */
    public int indexOf(int ch) {
        return indexOf(ch, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character, starting the search at the specified index.
     * <p/>
     * If a character with value <code>ch</code> occurs in the character
     * sequence represented by this <code>String</code> object at an index
     * no smaller than <code>fromIndex</code>, then the index of the first
     * such occurrence is returned--that is, the smallest value <i>k</i>
     * such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) && (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. If no such character occurs in this string at or after
     * position <code>fromIndex</code>, then <code>-1</code> is returned.
     * <p/>
     * There is no restriction on the value of <code>fromIndex</code>. If it
     * is negative, it has the same effect as if it were zero: this entire
     * string may be searched. If it is greater than the length of this
     * string, it has the same effect as if it were equal to the length of
     * this string: <code>-1</code> is returned.
     *
     * @param ch        a character.
     * @param fromIndex the index to start the search from.
     * @return the index of the first occurrence of the character in the
     *         character sequence represented by this object that is greater
     *         than or equal to <code>fromIndex</code>, or <code>-1</code>
     *         if the character does not occur.
     */
    public int indexOf(int ch, int fromIndex) {
        int max = offset + count;
        byte v[] = value;

        if (fromIndex < 0) {
            fromIndex = 0;
        }
        else if (fromIndex >= count) {
            // Note: fromIndex might be near -1>>>1.
            return -1;
        }
        for (int i = offset + fromIndex; i < max; i++) {
            if (v[i] == ch) {
                return i - offset;
            }
        }
        return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified character. That is, the index returned is the largest
     * value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true.
     * The String is searched backwards starting at the last character.
     *
     * @param ch a character.
     * @return the index of the last occurrence of the character in the
     *         character sequence represented by this object, or
     *         <code>-1</code> if the character does not occur.
     */
    public int lastIndexOf(int ch) {
        return lastIndexOf(ch, count - 1);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified character, searching backward starting at the specified
     * index. That is, the index returned is the largest value <i>k</i>
     * such that:
     * <blockquote><pre>
     * this.charAt(k) == ch) && (k &lt;= fromIndex)
     * </pre></blockquote>
     * is true.
     *
     * @param ch        a character.
     * @param fromIndex the index to start the search from. There is no
     *                  restriction on the value of <code>fromIndex</code>. If it is
     *                  greater than or equal to the length of this string, it has
     *                  the same effect as if it were equal to one less than the
     *                  length of this string: this entire string may be searched.
     *                  If it is negative, it has the same effect as if it were -1:
     *                  -1 is returned.
     * @return the index of the last occurrence of the character in the
     *         character sequence represented by this object that is less
     *         than or equal to <code>fromIndex</code>, or <code>-1</code>
     *         if the character does not occur before that point.
     */
    public int lastIndexOf(int ch, int fromIndex) {
        int min = offset;
        byte v[] = value;

        for (int i = offset + ((fromIndex >= count) ? count - 1 : fromIndex); i >= min; i--) {
            if (v[i] == ch) {
                return i - offset;
            }
        }
        return -1;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring. The integer returned is the smallest value
     * <i>k</i> such that:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * is <code>true</code>.
     *
     * @param str any string.
     * @return if the string argument occurs as a substring within this
     *         object, then the index of the first character of the first
     *         such substring is returned; if it does not occur as a
     *         substring, <code>-1</code> is returned.
     */
    public int indexOf(OctetString str) {
        return indexOf(str, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.  The integer
     * returned is the smallest value <tt>k</tt> for which:
     * <blockquote><pre>
     *     k &gt;= Math.min(fromIndex, str.length()) && this.startsWith(str, k)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then -1 is returned.
     *
     * @param str       the substring for which to search.
     * @param fromIndex the index from which to start the search.
     * @return the index within this string of the first occurrence of the
     *         specified substring, starting at the specified index.
     */
    public int indexOf(OctetString str, int fromIndex) {
        return indexOf(value, offset, count,
            str.value, str.offset, str.count, fromIndex);
    }

    /**
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param targetOffset offset of the target string.
     * @param targetCount  count of the target string.
     * @param fromIndex    the index to begin searching from.
     */
    static int indexOf(byte[] source, int sourceOffset, int sourceCount,
                       byte[] target, int targetOffset, int targetCount,
                       int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        byte first = target[targetOffset];
        int i = sourceOffset + fromIndex;
        int max = sourceOffset + (sourceCount - targetCount);

        startSearchForFirstChar:
        while (true) {
            /* Look for first character. */
            while (i <= max && source[i] != first) {
                i++;
            }
            if (i > max) {
                return -1;
            }

            /* Found first character, now look at the rest of v2 */
            int j = i + 1;
            int end = j + targetCount - 1;
            int k = targetOffset + 1;
            while (j < end) {
                if (source[j++] != target[k++]) {
                    i++;
                    /* Look for str's first char again. */
                    continue startSearchForFirstChar;
                }
            }
            return i - sourceOffset;    /* Found whole string. */
        }
    }

    /**
     * Returns the index within this string of the rightmost occurrence
     * of the specified substring.  The rightmost empty string "" is
     * considered to occur at the index value <code>this.length()</code>.
     * The returned index is the largest value <i>k</i> such that
     * <blockquote><pre>
     * this.startsWith(str, k)
     * </pre></blockquote>
     * is true.
     *
     * @param str the substring to search for.
     * @return if the string argument occurs one or more times as a substring
     *         within this object, then the index of the first character of
     *         the last such substring is returned. If it does not occur as
     *         a substring, <code>-1</code> is returned.
     */
    public int lastIndexOf(OctetString str) {
        return lastIndexOf(str, count);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring, searching backward starting at the specified index.
     * The integer returned is the largest value <i>k</i> such that:
     * <blockquote><pre>
     *     k &lt;= Math.min(fromIndex, str.length()) && this.startsWith(str, k)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then -1 is returned.
     *
     * @param str       the substring to search for.
     * @param fromIndex the index to start the search from.
     * @return the index within this string of the last occurrence of the
     *         specified substring.
     */
    public int lastIndexOf(OctetString str, int fromIndex) {
        return lastIndexOf(value, offset, count,
            str.value, str.offset, str.count, fromIndex);
    }

    /**
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param targetOffset offset of the target string.
     * @param targetCount  count of the target string.
     * @param fromIndex    the index to begin searching from.
     */
    static int lastIndexOf(byte[] source, int sourceOffset, int sourceCount,
                           byte[] target, int targetOffset, int targetCount,
                           int fromIndex) {
        /*
     * Check arguments; return immediately where possible. For
     * consistency, don't check for null str.
     */
        int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (targetCount == 0) {
            return fromIndex;
        }

        int strLastIndex = targetOffset + targetCount - 1;
        byte strLastChar = target[strLastIndex];
        int min = sourceOffset + targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            while (i >= min && source[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (source[j--] != target[k--]) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start - sourceOffset + 1;
        }
    }

    /**
     * Returns a new string that is a substring of this string. The
     * substring begins with the character at the specified index and
     * extends to the end of this string. <p>
     * Examples:
     * <blockquote><pre>
     * "unhappy".substring(2) returns "happy"
     * "Harbison".substring(3) returns "bison"
     * "emptiness".substring(9) returns "" (an empty string)
     * </pre></blockquote>
     *
     * @param beginIndex the beginning index, inclusive.
     * @return the specified substring.
     * @throws IndexOutOfBoundsException if
     *                                   <code>beginIndex</code> is negative or larger than the
     *                                   length of this <code>String</code> object.
     */
    public OctetString substring(int beginIndex) {
        return substring(beginIndex, count);
    }

    /**
     * Returns a new string that is a substring of this string. The
     * substring begins at the specified <code>beginIndex</code> and
     * extends to the character at index <code>endIndex - 1</code>.
     * Thus the length of the substring is <code>endIndex-beginIndex</code>.
     * <p/>
     * Examples:
     * <blockquote><pre>
     * "hamburger".substring(4, 8) returns "urge"
     * "smiles".substring(1, 5) returns "mile"
     * </pre></blockquote>
     *
     * @param beginIndex the beginning index, inclusive.
     * @param endIndex   the ending index, exclusive.
     * @return the specified substring.
     * @throws IndexOutOfBoundsException if the
     *                                   <code>beginIndex</code> is negative, or
     *                                   <code>endIndex</code> is larger than the length of
     *                                   this <code>String</code> object, or
     *                                   <code>beginIndex</code> is larger than
     *                                   <code>endIndex</code>.
     */
    public OctetString substring(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > count) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        if (beginIndex > endIndex) {
            throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
        }
        return ((beginIndex == 0) && (endIndex == count)) ? this :
               new OctetString(offset + beginIndex, endIndex - beginIndex, value);
    }

    /**
     * Returns a new character sequence that is a subsequence of this sequence.
     * <p/>
     * <p> An invocation of this method of the form
     * <p/>
     * <blockquote><pre>
     * str.subSequence(begin,&nbsp;end)</pre></blockquote>
     * <p/>
     * behaves in exactly the same way as the invocation
     * <p/>
     * <blockquote><pre>
     * str.substring(begin,&nbsp;end)</pre></blockquote>
     * <p/>
     * This method is defined so that the <tt>String</tt> class can implement
     * the {@link CharSequence} interface. </p>
     *
     * @param beginIndex the begin index, inclusive.
     * @param endIndex   the end index, exclusive.
     * @return the specified subsequence.
     * @throws IndexOutOfBoundsException if <tt>beginIndex</tt> or <tt>endIndex</tt> are negative,
     *                                   if <tt>endIndex</tt> is greater than <tt>length()</tt>,
     *                                   or if <tt>beginIndex</tt> is greater than <tt>startIndex</tt>
     * @since 1.4
     */
    public CharSequence subSequence(int beginIndex, int endIndex) {
        return this.substring(beginIndex, endIndex);
    }

    /**
     * Concatenates the specified string to the end of this string.
     * <p/>
     * If the length of the argument string is <code>0</code>, then this
     * <code>String</code> object is returned. Otherwise, a new
     * <code>String</code> object is created, representing a character
     * sequence that is the concatenation of the character sequence
     * represented by this <code>String</code> object and the character
     * sequence represented by the argument string.<p>
     * Examples:
     * <blockquote><pre>
     * "cares".concat("s") returns "caress"
     * "to".concat("get").concat("her") returns "together"
     * </pre></blockquote>
     *
     * @param str the <code>String</code> that is concatenated to the end
     *            of this <code>String</code>.
     * @return a string that represents the concatenation of this object's
     *         characters followed by the string argument's characters.
     */
    public OctetString concat(OctetString str) {
        int otherLen = str.length();
        if (otherLen == 0) {
            return this;
        }
        byte buf[] = new byte[count + otherLen];
        getBytes(0, count, buf, 0);
        str.getBytes(0, otherLen, buf, count);
        return new OctetString(0, count + otherLen, buf);
    }

    /**
     * Returns a new string resulting from replacing all occurrences of
     * <code>oldChar</code> in this string with <code>newChar</code>.
     * <p/>
     * If the character <code>oldChar</code> does not occur in the
     * character sequence represented by this <code>String</code> object,
     * then a reference to this <code>String</code> object is returned.
     * Otherwise, a new <code>String</code> object is created that
     * represents a character sequence identical to the character sequence
     * represented by this <code>String</code> object, except that every
     * occurrence of <code>oldChar</code> is replaced by an occurrence
     * of <code>newChar</code>.
     * <p/>
     * Examples:
     * <blockquote><pre>
     * "mesquite in your cellar".replace('e', 'o')
     *         returns "mosquito in your collar"
     * "the war of baronets".replace('r', 'y')
     *         returns "the way of bayonets"
     * "sparring with a purple porpoise".replace('p', 't')
     *         returns "starring with a turtle tortoise"
     * "JonL".replace('q', 'x') returns "JonL" (no change)
     * </pre></blockquote>
     *
     * @param oldChar the old character.
     * @param newChar the new character.
     * @return a string derived from this string by replacing every
     *         occurrence of <code>oldChar</code> with <code>newChar</code>.
     */
    public OctetString replace(byte oldChar, byte newChar) {
        if (oldChar != newChar) {
            int len = count;
            int i = -1;
            byte[] val = value; /* avoid getfield opcode */
            int off = offset;   /* avoid getfield opcode */

            while (++i < len) {
                if (val[off + i] == oldChar) {
                    break;
                }
            }
            if (i < len) {
                byte buf[] = new byte[len];
                System.arraycopy(val, off, buf, 0, i);
                while (i < len) {
                    byte c = val[off + i];
                    buf[i] = (c == oldChar) ? newChar : c;
                    i++;
                }
                return new OctetString(0, len, buf);
            }
        }
        return this;
    }

    /**
     * Tells whether or not this string matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a>.
     * <p/>
     * <p> An invocation of this method of the form
     * <i>str</i><tt>.matches(</tt><i>regex</i><tt>)</tt> yields exactly the
     * same result as the expression
     * <p/>
     * <blockquote><tt> {@link Pattern}.{@link
     * Pattern#matches(String, CharSequence)
     * matches}(</tt><i>regex</i><tt>,</tt> <i>str</i><tt>)</tt></blockquote>
     *
     * @param regex the regular expression to which this string is to be matched
     * @return <tt>true</tt> if, and only if, this string matches the
     *         given regular expression
     * @throws java.util.regex.PatternSyntaxException
     *          if the regular expression's syntax is invalid
     * @see Pattern
     * @since 1.4
     */
    public boolean matches(String regex) {
        return Pattern.matches(regex, this);
    }

    /**
     * Converts all of the characters in this <code>String</code> to lower
     * case using the rules of the default locale. This is equivalent to calling
     * <code>toLowerCase(Locale.getDefault())</code>.
     * <p/>
     *
     * @return the <code>String</code>, converted to lowercase.
     */
    public OctetString toLowerCase() {
        int len = count;
        int off = offset;
        byte[] val = value;
        int firstUpper;

        /* Now check if there are any characters that need to be changed. */
        scan:
        {
            for (firstUpper = 0; firstUpper < len; firstUpper++) {
                byte c = value[off + firstUpper];
                if (c != Octet.toLowerCase(c)) {
                    break scan;
                }
            }
            return this;
        }

        byte[] result = new byte[count];

        /* Just copy the first few lowerCase characters. */
        System.arraycopy(val, off, result, 0, firstUpper);

        for (int i = firstUpper; i < len; ++i) {
            result[i] = Octet.toLowerCase(val[off + i]);
        }

        return new OctetString(result);
    }

    /**
     * 将OctetString直接转成小写，不构建新的对象
     *
     * @param str
     */
    public static void toLowerCase(OctetString str) {
        byte[] val = str.value;
        int off = str.offset;
        int len = off + str.count;
        for (; off < len; off++) {
            val[off] = Octet.toLowerCase(val[off]);
        }
    }

    /**
     * Converts all of the characters in this <code>String</code> to upper
     * case using the rules of the default locale. This method is equivalent to
     * <code>toUpperCase(Locale.getDefault())</code>.
     * <p/>
     *
     * @return the <code>String</code>, converted to uppercase.
     */
    public OctetString toUpperCase() {
        int len = count;
        int off = offset;
        byte[] val = value;
        int firstLower;

        scan:
        {
            for (firstLower = 0; firstLower < len; firstLower++) {
                byte c = value[off + firstLower];
                if (c != Octet.toUpperCase(c)) {
                    break scan;
                }
            }
            return this;
        }

        byte[] result = new byte[len]; /* might grow! */
        int resultOffset = 0;

        System.arraycopy(val, off, result, 0, firstLower);

        // normal, fast loop
        for (int i = firstLower; i < len; ++i) {
            byte ch = val[off + i];
            result[i + resultOffset] = Octet.toUpperCase(ch);
        }
        return new OctetString(result);
    }

    /**
     * 转换成大写字母，不产生新的对象
     *
     * @param str 字符串
     */
    public static void toUpperCase(OctetString str) {
        byte[] val = str.value;
        int off = str.offset;
        int len = off + str.count;
        for (; off < len; off++) {
            val[off] = Octet.toUpperCase(val[off]);
        }
    }

    /**
     * Returns a copy of the string, with leading and trailing whitespace
     * omitted.
     * <p/>
     * If this <code>String</code> object represents an empty character
     * sequence, or the first and last characters of character sequence
     * represented by this <code>String</code> object both have codes
     * greater than <code>'&#92;u0020'</code> (the space character), then a
     * reference to this <code>String</code> object is returned.
     * <p/>
     * Otherwise, if there is no character with a code greater than
     * <code>'&#92;u0020'</code> in the string, then a new
     * <code>String</code> object representing an empty string is created
     * and returned.
     * <p/>
     * Otherwise, let <i>k</i> be the index of the first character in the
     * string whose code is greater than <code>'&#92;u0020'</code>, and let
     * <i>m</i> be the index of the last character in the string whose code
     * is greater than <code>'&#92;u0020'</code>. A new <code>String</code>
     * object is created, representing the substring of this string that
     * begins with the character at index <i>k</i> and ends with the
     * character at index <i>m</i>-that is, the result of
     * <code>this.substring(<i>k</i>,&nbsp;<i>m</i>+1)</code>.
     * <p/>
     * This method may be used to trim
     * {@link Character#isSpace(char) whitespace} from the beginning and end
     * of a string; in fact, it trims all ASCII control characters as well.
     *
     * @return A copy of this string with leading and trailing white
     *         space removed, or this string if it has no leading or
     *         trailing white space.
     */
    public OctetString trim() {
        int len = count;
        int st = 0;
        int off = offset;      /* avoid getfield opcode */
        byte[] val = value;    /* avoid getfield opcode */

        while ((st < len) && (val[off + st] <= ' ')) {
            st++;
        }
        while ((st < len) && (val[off + len - 1] <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < count)) ? substring(st, len) : this;
    }

    /**
     * 直接Trim不产生新的对象
     *
     * @param str
     */
    public static void trim(OctetString str) {
        int off = str.offset;
        int len = off + str.count;
        byte[] val = str.value;

        int i = len - 1;

        while ((off < len) && Octet.needTrim(val[off])) {
            off++;
        }

        while ((i > off) && Octet.needTrim(val[i])) {
            i--;
        }
        if ((off > str.offset) || (i < len - 1)) {
            str.offset = off;
            str.count -= (len - i - 1);
        }
    }

    /**
     * This object (which is already a string!) is itself returned.
     *
     * @return the string itself.
     */
    public String toString() {
        return new String(value, offset, count);
    }

    /**
     * 得到起始位置
     *
     * @return 起始位置
     */
    public int offset() {
        return offset;
    }

    /**
     * 与#getBytes不同的是：#getValue返回原始的bytes[]
     * 所以一定要注意Offset和数据维护
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * Converts this string to a new character array.
     *
     * @return a newly allocated character array whose length is the length
     *         of this string and whose contents are initialized to contain
     *         the character sequence represented by this string.
     */
    public byte[] toByteArray() {
        byte result[] = new byte[count];
        getBytes(0, count, result, 0);
        return result;
    }

    public static OctetString valueOf(byte data[]) {
        return new OctetString(data);
    }

    public static OctetString valueOf(byte data[], int offset, int count) {
        return new OctetString(data, offset, count);
    }

    public static OctetString valueOf(boolean b) {
        return b ? new OctetString(BYTE_TRUE) : new OctetString(BYTE_FALSE);
    }

    public static OctetString valueOf(byte b) {
        return OctetFormat.toOctetString(b);
    }

    public static OctetString valueOf(int i) {
        return OctetFormat.toOctetString(i);
    }

    public static OctetString valueOf(long l) {
        return OctetFormat.toOctetString(l);
    }

    public static OctetString valueOf(float f) {
        return OctetFormat.toOctetString(f);
    }

    public static OctetString valueOf(double d) {
        return OctetFormat.toOctetString(d);
    }

    public static OctetString valueOf(String str) {
        return new OctetString(str);
    }

    public static OctetString valueOf(Object obj) {
        if (obj == null) {
            return NULL;
        }
        else {
            OctetString str;
            if (obj instanceof Number) {
                if (obj instanceof Integer) {
                    str = valueOf(((Integer)obj).intValue());
                }
                else if (obj instanceof Long) {
                    str = valueOf(((Long)obj).longValue());
                }
                else if (obj instanceof Byte) {
                    str = valueOf(((Byte)obj).byteValue());
                }
                else if (obj instanceof Short) {
                    str = valueOf(((Short)obj).shortValue());
                }
                else if (obj instanceof Float) {
                    str = valueOf(((Float)obj).floatValue());
                }
                else if (obj instanceof Double) {
                    str = valueOf(((Double)obj).doubleValue());
                }
                else {
                    str = valueOf(String.valueOf(obj));
                }
            }
            else {
                str = valueOf(String.valueOf(obj));
            }
            return str;
        }
    }

    public static OctetString valueOf(String str, String enc)
        throws UnsupportedEncodingException {
        return new OctetString(str.getBytes(enc));
    }

    /**
     * 取得数据(完整的不含空值)
     *
     * @return byte[]
     */
    public byte[] getBytes() {
        if (offset == 0 && count == value.length) {
            return value;
        }
        else {
            byte[] result = new byte[count];
            System.arraycopy(value, offset, result, 0, count);
            return result;
        }
    }

    /**
     * 返回数据大小
     *
     * @return
     */
    public int size() {
        return length();
    }

    /**
     * 输出
     *
     * @param output
     * @throws IOException
     */
    public void writeTo(OutputStream output)
        throws IOException {
        output.write(value, offset, count);
    }
}
