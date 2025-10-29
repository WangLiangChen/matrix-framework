package wang.liangchen.matrix.framework.commons.astronomy;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.lang.Math.*;

/**
 * <pre>{@code
 * {Α, α, Alpha, 阿尔法}
 * {Β, β, Beta, 贝塔}
 * {Γ, γ, Gamma, 伽马}
 * {Δ, δ, Delta, 德尔塔}
 * {Ε, ε, Epsilon, 艾普西隆}
 * {Ζ, ζ, Zeta, 泽塔}
 * {Η, η, Eta, 伊塔}
 * {Θ, θ, Theta, 西塔}
 * {Ι, ι, Iota, 约塔}
 * {Κ, κ, Kappa, 卡帕}
 * {Λ, λ, Lambda, 拉姆达}
 * {Μ, μ, Mu, 缪}
 * {Ν, ν, Nu, 纽}
 * {Ξ, ξ, Xi, 克西}
 * {Ο, ο, Omicron, 奥米克戎}
 * {Π, π, Pi, 派}
 * {Ρ, ρ, Rho, 柔}
 * {Σ, σ, Sigma, 西格玛}
 * {Τ, τ, Tau, 陶}
 * {Υ, υ, Upsilon, 宇普西隆}
 * {Φ, φ, Phi, 斐}
 * {Χ, χ, Chi, 希}
 * {Ψ, ψ, Psi, 普西}
 * {Ω, ω, Omega, 欧米伽}
 * }</pre>
 */
public enum AstronomicalAlgorithmUtil {
    INSTANCE;

    private static final double[] SOLAR_TERM_LONGITUDES = {
            0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150, 165,
            180, 195, 210, 225, 240, 255, 270, 285, 300, 315, 330, 345
    };

    private static final String[] SOLAR_TERM_NAMES = {
            "春分", "清明", "谷雨", "立夏", "小满", "芒种",
            "夏至", "小暑", "大暑", "立秋", "处暑", "白露",
            "秋分", "寒露", "霜降", "立冬", "小雪", "大雪",
            "冬至", "小寒", "大寒", "立春", "雨水", "惊蛰"
    };
    public static final double AU_METERS = 149597870700.0; // 天文单位，日地平均距离
    private static final double ARC_SEC_TO_DEG = 1.0 / 3600.0;
    private static final double DEG_TO_RAD = Math.PI / 180.0;
    private static final double RAD_TO_DEG = 180.0 / Math.PI;
    private static final double LIGHT_TIME_ABERRATION_DEGREE = -20.49552 * ARC_SEC_TO_DEG;
    private static final double J2000 = 2451545.0;
    private static final double DAYS_PER_JULIAN_CENTURY = 36525.0;
    private static final double EARTH_SEMI_MAJOR_AXIS_AU = 1.0000010178;

    /**
     * 均时差（分钟）
     */
    public double equationOfTimeMinutes(ZonedDateTime utc) {
        double T = utc2JulianCentury(utc);
        double[] eotComponents = equationOfTimeWithComponents(T);
        return eotComponents[0] * 4.0;
    }

    /**
     * eot
     */
    public double[] equationOfTimeWithComponents(double T) {
        // 太阳平黄经
        double L0 = meanSolarLongitude(T);
        // 太阳平近点角
        double M = meanSolarAnomaly(T);
        // 地球轨道偏心率
        double e = earthOrbitEccentricity(T);
        // 章动
        double[] nutation = nutation(T);
        double deltaPsi = nutation[0];
        double deltaEpsilon = nutation[1];

        double C = solarEquationOfCenter(M, e);
        // 光行差
        double aberration = aberration(M, C, e, T);
        // 太阳视黄经(包含中心差+黄经章动deltaPsi+光行差)
        double lambda = L0 + C + deltaPsi + aberration;

        // 真黄赤交角(含交角章动deltaEpsilon)
        double meanObliquity = meanObliquityOfEcliptic(T);
        double epsilon = meanObliquity + deltaEpsilon;

        // 太阳赤经
        double alpha = solarRightAscension(lambda, 0, epsilon);

        // 地球轨道偏心率导致的地球公转速度不均匀（速度分量）
        double E_eccentricity = -C;
        // 黄赤交角导致的太阳赤经变化率不均匀（倾斜分量）
        double E_obliquity = lambda - alpha;
        // 总均时差 0.0057183° = 20.6角秒
        double E_total = L0 - alpha - 0.00572222 + deltaPsi * cos(epsilon * DEG_TO_RAD);

        E_eccentricity = normalizeDegreesTo180(E_eccentricity);
        E_obliquity = normalizeDegreesTo180(E_obliquity);
        E_total = normalizeDegreesTo180(E_total);

        return new double[]{E_total, E_eccentricity, E_obliquity};
    }

    /**
     * Convert utc to julianDay
     *
     * @param utc utc date time
     * @return julian day
     */
    public double utc2JulianDay(ZonedDateTime utc) {
        if (!ZoneOffset.UTC.equals(utc.getOffset())) {
            throw new MatrixErrorException("The parameter 'utc' must be a UTC date time");
        }

        int year = utc.getYear();
        int month = utc.getMonthValue();
        int day = utc.getDayOfMonth();
        // 检查1582年10月的无效日期范围
        if (year == 1582 && month == 10 && day >= 5 && day <= 14) {
            throw new MatrixErrorException("date does not exist");
        }
        int yearAdjusted = (month > 2) ? year : year - 1;
        int monthAdjusted = (month > 2) ? month : month + 12;
        // 格里高利历修正
        int b = 0;
        if (year > 1582 || (year == 1582 && month > 10) || (year == 1582 && month == 10 && day >= 15)) {
            int a = (int) Math.floor(yearAdjusted / 100.0);
            b = 2 - a + (int) Math.floor(a / 4.0);
        }

        // 计算小数天,高精度计算（避免浮点累积误差）
        double fractionalDay = (utc.getHour() * 3600 + utc.getMinute() * 60 + utc.getSecond() +
                utc.getNano() * 1e-9) / 86400.0;

        return Math.floor(365.25 * (yearAdjusted + 4716)) +
                Math.floor(30.6001 * (monthAdjusted + 1)) +
                day + b + fractionalDay - 1524.5;
    }

    /**
     * 计算儒略世纪数
     */
    public double julianCentury(double julianDay) {
        return (julianDay - J2000) / DAYS_PER_JULIAN_CENTURY;
    }

    /**
     *
     * @param utc
     * @return
     */
    public double utc2JulianCentury(ZonedDateTime utc) {
        return julianCentury(utc2JulianDay(utc));
    }

    public double julianDay(double T) {
        return T * 36525.0 + 2451545.0;
    }

    /**
     * 太阳平黄经 L0
     */
    public double meanSolarLongitude(double T) {
        double L0 = 280.4664567
                + T * (36000.76982779
                + T * (0.0003032028
                + T * (1.0 / 49931.0
                - T * (1.0 / 15300.0
                + T * (1.0 / 2000000.0
                - T * (0.00000003
                + T * (0.0000000005)))))));
        return normalizeDegrees(L0);
    }

    /**
     * 太阳平近点角
     */
    public double meanSolarAnomaly(double T) {
        double M = 357.52910918
                + 35999.0502911 * T
                - 0.000153666 * T * T
                + 1.0 / 24490000.0 * T * T * T
                - 1.0 / 80000000.0 * T * T * T * T
                - 1.0 / 18700000000.0 * T * T * T * T * T;
        return normalizeDegrees(M);
    }

    /**
     * 地球轨道偏心率 e
     */
    public double earthOrbitEccentricity(double T) {
        return 0.0167086342
                - 0.0000420374 * T
                - 0.0000001267 * T * T
                + 1.4e-10 * T * T * T
                - 5.0e-13 * T * T * T * T
                + 1.0e-15 * T * T * T * T * T;
    }

    /**
     * 太阳中心差由于地球轨道偏心引起 C
     */

    public double solarEquationOfCenter(double T) {
        double M = meanSolarAnomaly(T);
        double e = earthOrbitEccentricity(T);
        return solarEquationOfCenter(M, e);
    }

    public double solarEquationOfCenter(double M, double e) {
        double M_rad = M * DEG_TO_RAD;
        // return Math.sin(M_rad) * (1.914602 - T * (0.004817 + 0.000014 * T)) + Math.sin(2 * M_rad) * (0.019993 - 0.000101 * T) + Math.sin(3 * M_rad) * 0.000289;
        double C = (2 * e - 0.25 * Math.pow(e, 3) + 5.0 / 96.0 * Math.pow(e, 5)) * Math.sin(M_rad)
                + (1.25 * Math.pow(e, 2) - 11.0 / 24.0 * Math.pow(e, 4)) * Math.sin(2 * M_rad)
                + (13.0 / 12.0 * Math.pow(e, 3) - 43.0 / 64.0 * Math.pow(e, 5)) * Math.sin(3 * M_rad)
                + (103.0 / 96.0 * Math.pow(e, 4)) * Math.sin(4 * M_rad)
                + (1097.0 / 960.0 * Math.pow(e, 5)) * Math.sin(5 * M_rad);

        return C * RAD_TO_DEG;
    }

    /**
     * 太阳真黄经
     */
    public double trueSolarLongitude(double T) {
        double L0 = meanSolarLongitude(T);
        double C = solarEquationOfCenter(T);
        return L0 + C;
    }

    /**
     * 太阳真近点角
     */
    public double trueSolarAnomaly(double T) {
        double M = meanSolarAnomaly(T);
        double C = solarEquationOfCenter(T);
        return M + C;
    }

    /**
     * 太阳视黄经 lambda
     */
    public double apparentSolarLongitude(double T) {
        double O = trueSolarLongitude(T);
        var omega = lunarNodeLongitude(T);
        return O - 0.00569 - 0.00478 * Math.sin(omega * DEG_TO_RAD);
    }

    /**
     * 计算日地距离
     *
     * @param M 平近点角（角度）
     * @param e 偏心率
     * @return 日地距离（AU）
     */
    public double solarEarthDistance(double M, double C, double e, double T) {
        double trueSolarAnomaly = M + C;
        double nu_rad = trueSolarAnomaly * DEG_TO_RAD;
        // in AUs
        return (earthSemiMajorAxis(T) * (1 - e * e)) / (1 + e * Math.cos(nu_rad));
    }

    public double earthSemiMajorAxis(double T) {
        return EARTH_SEMI_MAJOR_AXIS_AU * (1 - 4e-8 * T);
    }

    /**
     * 平黄赤交角 epsilon
     */
    public double meanObliquityOfEcliptic(double T) {
        // IAU 2006公式（角秒）
        double epsilon_arcsec = 84381.406
                - 46.836769 * T
                - 0.0001831 * T * T
                + 0.00200340 * T * T * T
                - 0.000000576 * T * T * T * T
                - 0.0000000434 * T * T * T * T * T;
        // 转换为度
        return epsilon_arcsec * ARC_SEC_TO_DEG;
    }

    double apparentObliquity(double T) {
        double e0 = meanObliquityOfEcliptic(T);
        double omega = lunarNodeLongitude(T);
        return e0 + 0.00256 * Math.cos(omega * DEG_TO_RAD);        // in degrees
    }

    /**
     * 太阳赤经
     */
    public double solarRightAscension(double lambda, double beta, double epsilon) {
        // beta=0 黄纬
        double lambda_rad = lambda * DEG_TO_RAD;
        double epsilon_rad = epsilon * DEG_TO_RAD;
        double beta_rad = beta * DEG_TO_RAD;

        // 球面三角公式α = atan2(sinλ·cosε - tanβ·sinε, cosλ)
        double tananum = sin(lambda_rad) * cos(epsilon_rad) - tan(beta_rad) * sin(epsilon_rad);
        double tanadenom = cos(lambda_rad);

        double alpha_rad = Math.atan2(tananum, tanadenom);
        double alpha_deg = alpha_rad * RAD_TO_DEG;

        return normalizeDegrees(alpha_deg);
    }

    /**
     * 太阳赤纬
     */
    public double solarDeclination(double T) {
        var e = apparentObliquity(T);
        var lambda = apparentSolarLongitude(T);
        var sint = Math.sin(e * DEG_TO_RAD) * Math.sin(lambda * DEG_TO_RAD);
        return Math.asin(sint) * RAD_TO_DEG;
    }

    /**
     * 日出时角 HA
     */
    public double hourAngleAtSunrise(double lat, double solarDeclination) {
        var latRad = lat * DEG_TO_RAD;
        var sdRad = solarDeclination * DEG_TO_RAD;
        var HAarg = (Math.cos(90.833 * DEG_TO_RAD) / (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad) * Math.tan(sdRad));
        // in radians (for sunset, use -HA)
        return Math.acos(HAarg);
    }

    /**
     *
     */
    public ZonedDateTime solarTerm(int year, int termIndex) {
        // 估算节气的初始时间（UTC）
        ZonedDateTime estimatedTime = estimateSolarTermTime(year, termIndex);
        double targetLongitude = SOLAR_TERM_LONGITUDES[termIndex]; // 目标黄经
        // 2. 牛顿迭代参数
        double threshold = 1e-8;
        int maxIterations = 10;

        for (int i = 0; i < maxIterations; i++) {
            double T = utc2JulianCentury(estimatedTime);
            // 计算当前时间的太阳真黄经
            double lambda = apparentSolarLongitude(T);
            // 计算与目标黄经的差值
            double delta = normalizeDegreesTo180(lambda - targetLongitude);
            // 若差值小于阈值，结束迭代
            if (Math.abs(delta) < threshold) {
                break;
            }

            // 中心差分法
            ZonedDateTime minusTime = estimatedTime.minusMinutes(5);
            ZonedDateTime plusTime = estimatedTime.plusMinutes(5);
            double minusT = utc2JulianCentury(minusTime);
            double plusT = utc2JulianCentury(plusTime);
            double minusLambda = apparentSolarLongitude(minusT);
            double plusLambda = apparentSolarLongitude(plusT);
            // 度/秒
            double rate = normalizeDegreesTo180(plusLambda - minusLambda) / (300 * 2);
            // 避免除零错误
            if (Math.abs(rate) < 1e-6) {
                rate = rate >= 0 ? 1e-6 : -1e-6;
            }

            // 4. 时间调整（高精度）牛顿迭代公式：Δt = -f(t)/f'(t)
            double totalSeconds = -delta / rate;
            long wholeSeconds = Math.round(totalSeconds);
            int nanos = (int) ((totalSeconds - wholeSeconds) * 1e9);
            estimatedTime = estimatedTime.plusSeconds(wholeSeconds).plusNanos(nanos);
        }

        return estimatedTime;
    }

    /**
     *
     * 月亮升交点平黄经 Omega
     * 月球轨道从南向北穿越黄道面的交点
     */
    public double lunarNodeLongitude(double T) {
        double Omega = 125.04455501 - 1934.13626197 * T + 0.0020756 * T * T + T * T * T / 467441.0 - T * T * T * T / 60616000.0;
        return normalizeDegrees(Omega);
    }

    /**
     * 章动,地球自转轴摆动
     */
    private double[] nutation(double T) {
        // 日月平角距
        double D = normalizeDegrees(297.85036 + T * (445267.111480 + T * (-0.0019142 + T / 189474.0)));
        // 太阳平近点角
        double M = meanSolarAnomaly(T);
        // 月亮平近点角
        double M_prime = normalizeDegrees(134.96298 + T * (477198.867398 + T * (0.0086972 + T / 56250.0)));
        // 月亮平黄经减去月亮升交点黄经-月亮纬度参数
        double F = normalizeDegrees(93.27191 + T * (483202.017538 + T * (-0.0036825 + T / 327270.0)));
        // 月亮升交点平黄经
        double Omega = lunarNodeLongitude(T);

        double Omega_rad = Omega * DEG_TO_RAD;
        double F_rad = F * DEG_TO_RAD;
        double D_rad = D * DEG_TO_RAD;
        double M_rad = M * DEG_TO_RAD;
        double M_prime_rad = M_prime * DEG_TO_RAD;

        // 黄经章动 (Δψ)
        double deltaPsi = -17.1996 * sin(Omega_rad)
                + 0.2062 * sin(2 * Omega_rad)
                - 1.3187 * sin(2 * F_rad - 2 * D_rad + 2 * Omega_rad)
                + 0.2274 * sin(2 * F_rad + 2 * Omega_rad)
                + 0.2070 * sin(M_rad)
                + 0.1426 * sin(M_prime_rad)
                + 0.0517 * sin(2 * F_rad - 2 * D_rad + M_rad)
                + 0.0712 * sin(2 * D_rad)
                - 0.0307 * sin(2 * F_rad + 2 * D_rad + 2 * Omega_rad)
                + 0.0224 * sin(2 * F_rad - 2 * D_rad + 2 * Omega_rad + M_rad);

        // 交角章动 (Δε)
        double deltaEpsilon = 9.2025 * cos(Omega_rad)
                + 0.0895 * cos(2 * Omega_rad)
                - 0.5736 * cos(2 * F_rad - 2 * D_rad + 2 * Omega_rad)
                - 0.0977 * cos(2 * F_rad + 2 * Omega_rad)
                + 0.0900 * cos(M_rad)
                + 0.0549 * cos(M_prime_rad)
                + 0.0279 * cos(2 * F_rad - 2 * D_rad + M_rad)
                + 0.0154 * cos(2 * D_rad)
                + 0.0128 * cos(2 * F_rad + 2 * D_rad + 2 * Omega_rad)
                + 0.0075 * cos(2 * F_rad - 2 * D_rad + 2 * Omega_rad + M_rad);

        // 补充更多章动项（实际实现需要1066项）
        deltaPsi += -0.0153 * sin(2 * D_rad - M_rad);
        deltaEpsilon += 0.0066 * cos(2 * D_rad - M_rad);

        deltaPsi += 0.0129 * sin(M_prime_rad + 2 * F_rad - 2 * D_rad + 2 * Omega_rad);
        deltaEpsilon += 0.0056 * cos(M_prime_rad + 2 * F_rad - 2 * D_rad + 2 * Omega_rad);

        deltaPsi += -0.0117 * sin(Omega_rad + M_rad);
        deltaEpsilon += 0.0051 * cos(Omega_rad + M_rad);

        deltaPsi += 0.0107 * sin(2 * F_rad + 2 * Omega_rad + M_prime_rad);
        deltaEpsilon += 0.0046 * cos(2 * F_rad + 2 * Omega_rad + M_prime_rad);

        deltaPsi += -0.0101 * sin(2 * F_rad - 2 * D_rad + 2 * Omega_rad - M_rad);
        deltaEpsilon += 0.0044 * cos(2 * F_rad - 2 * D_rad + 2 * Omega_rad - M_rad);

        deltaPsi += 0.0096 * sin(2 * D_rad - 2 * Omega_rad);
        deltaEpsilon += -0.0042 * cos(2 * D_rad - 2 * Omega_rad);

        deltaPsi += -0.0082 * sin(3 * M_prime_rad);
        deltaEpsilon += 0.0036 * cos(3 * M_prime_rad);

        deltaPsi += 0.0080 * sin(M_rad - 2 * D_rad);
        deltaEpsilon += -0.0035 * cos(M_rad - 2 * D_rad);

        // 行星章动项（14项）
        deltaPsi += -0.0044 * sin(2 * F_rad - 3 * M_prime_rad);
        deltaEpsilon += 0.0019 * cos(2 * F_rad - 3 * M_prime_rad);

        deltaPsi += 0.0041 * sin(2 * F_rad + M_prime_rad);
        deltaEpsilon += -0.0018 * cos(2 * F_rad + M_prime_rad);

        deltaPsi += -0.0040 * sin(Omega_rad - M_prime_rad);
        deltaEpsilon += 0.0017 * cos(Omega_rad - M_prime_rad);

        // 添加时间相关项
        deltaPsi += T * (-0.00052 * sin(Omega_rad) + 0.00015 * sin(2 * D_rad));
        deltaEpsilon += T * (0.00029 * cos(Omega_rad) - 0.00006 * cos(2 * D_rad));

        return new double[]{deltaPsi * ARC_SEC_TO_DEG, deltaEpsilon * ARC_SEC_TO_DEG};
    }


    /**
     * 光行差
     *
     */
    private double aberration(double M, double C, double e, double T) {
        double R = solarEarthDistance(M, C, e, T);
        return LIGHT_TIME_ABERRATION_DEGREE / R;
    }


    private boolean isLeapYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }

    /**
     * 使用平气法估算节气时间
     */
    private ZonedDateTime estimateSolarTermTime(int year, int termIndex) {
        // 以春分点（3月20日左右）为基准
        ZonedDateTime vernalEquinoxApprox = ZonedDateTime.of(year, 3, 19, 12, 0, 0, 0, ZoneOffset.UTC);
        // 平气间隔：365.2422/24 ≈ 15.2184天
        double meanTermInterval = 365.2422 / 24.0;
        long daysOffset = Math.round(termIndex * meanTermInterval);
        return vernalEquinoxApprox.plusDays(daysOffset);
    }


    /**
     * 应用岁差修正
     */
    private double precession(double T) {
        double P = 5028.796195 * T
                + 1.1054348 * T * T
                + 0.00007964 * T * T * T
                - 0.000023857 * T * T * T * T
                - 0.0000000383 * T * T * T * T * T;
        // 岁差是随时间线性增长的累积量，归一化会破坏时间连续性
        return P * ARC_SEC_TO_DEG;
    }

    private double normalizeDegrees(double degrees) {
        degrees = ((degrees % 360) + 360) % 360;
        return Math.abs(degrees - 360.0) < 1e-12 ? 0.0 : degrees;
    }

    private double normalizeRadians(double radians) {
        radians %= 2 * Math.PI;
        if (radians < 0) radians += 2 * Math.PI;
        return radians;
    }


    private double normalizeDegreesTo180(double degrees) {
        degrees %= 360;
        if (degrees > 180) degrees -= 360;
        if (degrees < -180) degrees += 360;
        if (degrees == -180) degrees = 180;
        return degrees;
    }
}