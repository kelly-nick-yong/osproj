import java.io.PrintStream;

class Format {
    private int width = 0;
    private int precision = -1;
    private String pre = "";
    private String post = "";
    private boolean leading_zeroes = false;
    private boolean show_plus = false;
    private boolean alternate = false;
    private boolean show_space = false;
    private boolean left_align = false;
    private char fmt = 32;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Format(String string) {
        boolean bl = false;
        int n = string.length();
        int n2 = 0;
        int n3 = 0;
        while (n2 == 0) {
            if (n3 >= n) {
                n2 = 5;
            } else if (string.charAt(n3) == '%') {
                if (n3 >= n - 1) throw new IllegalArgumentException();
                if (string.charAt(n3 + 1) == '%') {
                    this.pre = String.valueOf(this.pre) + '%';
                    ++n3;
                } else {
                    n2 = 1;
                }
            } else {
                this.pre = String.valueOf(this.pre) + string.charAt(n3);
            }
            ++n3;
        }
        while (n2 == 1) {
            if (n3 >= n) {
                n2 = 5;
            } else if (string.charAt(n3) == ' ') {
                this.show_space = true;
            } else if (string.charAt(n3) == '-') {
                this.left_align = true;
            } else if (string.charAt(n3) == '+') {
                this.show_plus = true;
            } else if (string.charAt(n3) == '0') {
                this.leading_zeroes = true;
            } else if (string.charAt(n3) == '#') {
                this.alternate = true;
            } else {
                n2 = 2;
                --n3;
            }
            ++n3;
        }
        while (n2 == 2) {
            if (n3 >= n) {
                n2 = 5;
                continue;
            }
            if (string.charAt(n3) >= '0' && string.charAt(n3) <= '9') {
                this.width = this.width * 10 + string.charAt(n3) - 48;
                ++n3;
                continue;
            }
            if (string.charAt(n3) == '.') {
                n2 = 3;
                this.precision = 0;
                ++n3;
                continue;
            }
            n2 = 4;
        }
        while (n2 == 3) {
            if (n3 >= n) {
                n2 = 5;
                continue;
            }
            if (string.charAt(n3) >= '0' && string.charAt(n3) <= '9') {
                this.precision = this.precision * 10 + string.charAt(n3) - 48;
                ++n3;
                continue;
            }
            n2 = 4;
        }
        if (n2 == 4) {
            if (n3 >= n) {
                n2 = 5;
            } else {
                this.fmt = string.charAt(n3);
            }
            ++n3;
        }
        if (n3 >= n) return;
        this.post = string.substring(n3, n);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public static double atof(String var0) {
        var2_2 = 1;
        var3_3 = 0.0;
        var5_4 = 0.0;
        var7_5 = 1.0;
        var9_6 = false;
        for (var1_1 = 0; var1_1 < var0.length() && Character.isWhitespace(var0.charAt(var1_1)); ++var1_1) {
        }
        if (var1_1 < var0.length() && var0.charAt(var1_1) == '-') {
            var2_2 = -1;
            ++var1_1;
        } else {
            ** if (var1_1 >= var0.length() || var0.charAt((int)var1_1) != '+') goto lbl13
        }
lbl13: // 4 sources:
        while (++var1_1 < var0.length()) {
            var10_7 = var0.charAt(var1_1);
            if (var10_7 >= '0' && var10_7 <= '9') {
                if (!var9_6) {
                    var3_3 = var3_3 * 10.0 + (double)var10_7 - 48.0;
                } else if (var9_6) {
                    var3_3+=(var7_5/=10.0) * (double)(var10_7 - 48);
                }
            } else if (var10_7 == '.') {
                if (var9_6 != false) return (double)var2_2 * var3_3;
                var9_6 = true;
            } else {
                if (var10_7 != 'e') {
                    if (var10_7 != 'E') return (double)var2_2 * var3_3;
                }
                var11_8 = (int)Format.parseLong(var0.substring(var1_1 + 1), 10);
                return (double)var2_2 * var3_3 * Math.pow(10.0, var11_8);
            }
            ++var1_1;
        }
        return (double)var2_2 * var3_3;
    }

    public static int atoi(String string) {
        return (int)Format.atol(string);
    }

    public static long atol(String string) {
        int n;
        for (n = 0; n < string.length() && Character.isWhitespace(string.charAt(n)); ++n) {
        }
        if (n < string.length() && string.charAt(n) == '0') {
            if (n + 1 < string.length() && (string.charAt(n + 1) == 'x' || string.charAt(n + 1) == 'X')) {
                return Format.parseLong(string.substring(n + 2), 16);
            }
            return Format.parseLong(string, 8);
        }
        return Format.parseLong(string, 10);
    }

    private static String convert(long l, int n, int n2, String string) {
        if (l == 0) {
            return "0";
        }
        String string2 = "";
        while (l != 0) {
            string2 = String.valueOf(string.charAt((int)(l & (long)n2))) + string2;
            l>>>=n;
        }
        return string2;
    }

    private String exp_format(double d) {
        double d2;
        String string = "";
        int n = 0;
        double d3 = 1.0;
        for (d2 = d; d2 > 10.0; d2/=10.0) {
            ++n;
            d3/=10.0;
        }
        while (d2 < 1.0) {
            --n;
            d3*=10.0;
            d2*=10.0;
        }
        if ((this.fmt == 'g' || this.fmt == 'G') && n >= -4 && n < this.precision) {
            return this.fixed_format(d);
        }
        string = String.valueOf(string) + this.fixed_format(d*=d3);
        string = this.fmt == 'e' || this.fmt == 'g' ? String.valueOf(string) + "e" : String.valueOf(string) + "E";
        String string2 = "000";
        if (n >= 0) {
            string = String.valueOf(string) + "+";
            string2 = String.valueOf(string2) + n;
        } else {
            string = String.valueOf(string) + "-";
            string2 = String.valueOf(string2) + (- n);
        }
        return String.valueOf(string) + string2.substring(string2.length() - 3, string2.length());
    }

    private String fixed_format(double d) {
        String string = "";
        if (d > 9.223372036854776E18) {
            return this.exp_format(d);
        }
        long l = (long)(this.precision == 0 ? d + 0.5 : d);
        string = String.valueOf(string) + l;
        double d2 = d - (double)l;
        if (d2 >= 1.0 || d2 < 0.0) {
            return this.exp_format(d);
        }
        return String.valueOf(string) + this.frac_part(d2);
    }

    public String form(char c) {
        if (this.fmt != 'c') {
            throw new IllegalArgumentException();
        }
        String string = String.valueOf(c);
        return this.pad(string);
    }

    public String form(double d) {
        String string;
        if (this.precision < 0) {
            this.precision = 6;
        }
        int n = 1;
        if (d < 0.0) {
            d = - d;
            n = -1;
        }
        if (this.fmt == 'f') {
            string = this.fixed_format(d);
        } else if (this.fmt == 'e' || this.fmt == 'E' || this.fmt == 'g' || this.fmt == 'G') {
            string = this.exp_format(d);
        } else {
            throw new IllegalArgumentException();
        }
        return this.pad(this.sign(n, string));
    }

    public String form(long l) {
        String string;
        int n = 0;
        if (this.fmt == 'd' || this.fmt == 'i') {
            n = 1;
            if (l < 0) {
                l = - l;
                n = -1;
            }
            string = String.valueOf(l);
        } else if (this.fmt == 'o') {
            string = Format.convert(l, 3, 7, "01234567");
        } else if (this.fmt == 'x') {
            string = Format.convert(l, 4, 15, "0123456789abcdef");
        } else if (this.fmt == 'X') {
            string = Format.convert(l, 4, 15, "0123456789ABCDEF");
        } else {
            throw new IllegalArgumentException();
        }
        return this.pad(this.sign(n, string));
    }

    public String form(String string) {
        if (this.fmt != 's') {
            throw new IllegalArgumentException();
        }
        if (this.precision >= 0) {
            string = string.substring(0, this.precision);
        }
        return this.pad(string);
    }

    private String frac_part(double d) {
        String string = "";
        if (this.precision > 0) {
            double d2 = 1.0;
            String string2 = "";
            for (int i = 1; i <= this.precision && d2 <= 9.223372036854776E18; d2*=10.0, ++i) {
                string2 = String.valueOf(string2) + "0";
            }
            long l = (long)(d2 * d + 0.5);
            string = String.valueOf(string2) + l;
            string = string.substring(string.length() - this.precision, string.length());
        }
        if (this.precision > 0 || this.alternate) {
            string = "." + string;
        }
        if (!(this.fmt != 'G' && this.fmt != 'g' || this.alternate)) {
            int n;
            for (n = string.length() - 1; n >= 0 && string.charAt(n) == '0'; --n) {
            }
            if (n >= 0 && string.charAt(n) == '.') {
                --n;
            }
            string = string.substring(0, n + 1);
        }
        return string;
    }

    private String pad(String string) {
        String string2 = Format.repeat(' ', this.width - string.length());
        if (this.left_align) {
            return String.valueOf(this.pre) + string + string2 + this.post;
        }
        return String.valueOf(this.pre) + string2 + string + this.post;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private static long parseLong(String var0, int var1_1) {
        var3_3 = 1;
        var4_4 = 0;
        for (var2_2 = 0; var2_2 < var0.length() && Character.isWhitespace(var0.charAt(var2_2)); ++var2_2) {
        }
        if (var2_2 < var0.length() && var0.charAt(var2_2) == '-') {
            var3_3 = -1;
            ++var2_2;
        } else {
            ** if (var2_2 >= var0.length() || var0.charAt((int)var2_2) != '+') goto lbl10
        }
lbl10: // 4 sources:
        while (++var2_2 < var0.length()) {
            var6_5 = var0.charAt(var2_2);
            if (var6_5 >= '0' && var6_5 < 48 + var1_1) {
                var4_4 = var4_4 * (long)var1_1 + (long)var6_5 - 48;
            } else if (var6_5 >= 'A' && var6_5 < 65 + var1_1 - 10) {
                var4_4 = var4_4 * (long)var1_1 + (long)var6_5 - 65 + 10;
            } else {
                if (var6_5 < 'a') return var4_4 * (long)var3_3;
                if (var6_5 >= 97 + var1_1 - 10) return var4_4 * (long)var3_3;
                var4_4 = var4_4 * (long)var1_1 + (long)var6_5 - 97 + 10;
            }
            ++var2_2;
        }
        return var4_4 * (long)var3_3;
    }

    public static void print(PrintStream printStream, String string, char c) {
        printStream.print(new Format(string).form(c));
    }

    public static void print(PrintStream printStream, String string, double d) {
        printStream.print(new Format(string).form(d));
    }

    public static void print(PrintStream printStream, String string, long l) {
        printStream.print(new Format(string).form(l));
    }

    public static void print(PrintStream printStream, String string, String string2) {
        printStream.print(new Format(string).form(string2));
    }

    private static String repeat(char c, int n) {
        if (n <= 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(n);
        for (int i = 0; i < n; ++i) {
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    private String sign(int n, String string) {
        String string2 = "";
        if (n < 0) {
            string2 = "-";
        } else if (n > 0) {
            if (this.show_plus) {
                string2 = "+";
            } else if (this.show_space) {
                string2 = " ";
            }
        } else if (this.fmt == 'o' && this.alternate && string.length() > 0 && string.charAt(0) != '0') {
            string2 = "0";
        } else if (this.fmt == 'x' && this.alternate) {
            string2 = "0x";
        } else if (this.fmt == 'X' && this.alternate) {
            string2 = "0X";
        }
        int n2 = 0;
        if (this.leading_zeroes) {
            n2 = this.width;
        } else if ((this.fmt == 'd' || this.fmt == 'i' || this.fmt == 'x' || this.fmt == 'X' || this.fmt == 'o') && this.precision > 0) {
            n2 = this.precision;
        }
        return String.valueOf(string2) + Format.repeat('0', n2 - string2.length() - string.length()) + string;
    }
}