package wang.liangchen.matrix.framework.commons.astronomy;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public enum AstronomicalAlgorithmUtil {
    INSTANCE;

    // 使用更高精度的常量
    private static final double ARC_SEC_TO_DEG = 1.0 / 3600.0;
    private static final double DEG_TO_RAD = Math.PI / 180.0;
    private static final double RAD_TO_DEG = 180.0 / Math.PI;
    private static final double LIGHT_TIME_ABERRATION = -20.49552 * ARC_SEC_TO_DEG;
    private static final double J2000 = 2451545.0;
    private static final double DAYS_PER_CENTURY = 36525.0;

    /**
     * 高精度UTC到儒略日转换
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

        // 月份调整
        int y = (month > 2) ? year : year - 1;
        int m = (month > 2) ? month : month + 12;

        // 格里高利历修正
        int b = 0;
        if (year > 1582 || (year == 1582 && month > 10) || (year == 1582 && month == 10 && day >= 15)) {
            int a = INT(y / 100.0);
            b = 2 - a + INT(a / 4.0);
        }

        // 计算小数天,高精度计算（避免浮点累积误差）
        double fractionalDay = (utc.getHour() * 3600 +
                utc.getMinute() * 60 +
                utc.getSecond() +
                utc.getNano() / 1_000_000_000.0) / 86400.0;

        return INT(365.25 * (y + 4716)) +
                INT(30.6001 * (m + 1)) + b +
                day + fractionalDay - 1524.5;
    }

    /**
     * 计算儒略世纪数
     */
    public double julianCentury(double julianDay) {
        return (julianDay - J2000) / DAYS_PER_CENTURY;
    }

    public double uct2JulianCentury(ZonedDateTime utc) {
        return julianCentury(utc2JulianDay(utc));
    }

    /**
     * 太阳平黄经
     */
    public double meanSolarLongitude(double T) {
        double L0 = 280.4664567
                + T * (36000.76982779
                + T * (0.0003032028
                + T * (1.0 / 49931.0
                + T * (-1.0 / 15300.0
                + T * (1.0 / 2000000.0)))));
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
     * 地球轨道偏心率e
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
     * 牛顿迭代法求解开普勒方程
     *
     * @param M 平近点角（角度）
     * @param e 偏心率
     * @return 偏近点角E（弧度）
     */
    public double solveKeplerEquation(double M, double e) {
        // 将平近点角转换为弧度
        double M_rad = M * DEG_TO_RAD;
        // 初始猜测：E0 = M（对于小偏心率这是好的近似）
        double E = M_rad;
        double tolerance = 1e-8;
        double delta;
        int maxIterations = 100;
        int iteration = 0;
        do {
            // 开普勒方程：f(E) = E - e*sin(E) - M
            double f = E - e * Math.sin(E) - M_rad;
            // 导数：f'(E) = 1 - e*cos(E)
            double f_prime = 1 - e * Math.cos(E);
            // 牛顿迭代：E_{n+1} = E_n - f(E_n)/f'(E_n)
            delta = f / f_prime;
            E -= delta;
            iteration++;
            if (iteration > maxIterations) {
                throw new RuntimeException("开普勒方程未收敛，迭代次数超过" + maxIterations);
            }
        } while (Math.abs(delta) > tolerance);
        return E;
    }

    /**
     * 计算真近点角
     *
     * @param M 平近点角（角度）
     * @param e 偏心率
     * @return 真近点角ν（弧度）
     */
    public double trueSolarAnomaly(double M, double e) {
        // 求解开普勒方程得到偏近点角E
        double E_rad = solveKeplerEquation(M, e);
        // 公式：tan(ν/2) = √((1+e)/(1-e)) * tan(E/2)
        double tan_half_E = Math.tan(E_rad / 2);
        double sqrt_factor = Math.sqrt((1 + e) / (1 - e));
        double tan_half_nu = sqrt_factor * tan_half_E;

        return 2 * Math.atan(tan_half_nu);
    }

    /**
     * 计算日地距离
     *
     * @param M 平近点角（角度度）
     * @param e 偏心率
     * @return 日地距离（AU）
     */
    public double solarEarthDistance(double M, double e) {
        double nu_rad = trueSolarAnomaly(M, e);
        // 轨道方程：R = a * (1 - e²) / (1 + e * cos(ν))
        return 1.000001018 * (1 - e * e) / (1 + e * Math.cos(nu_rad));
    }

    /**
     * 太阳中心差是太阳平近点角与真近点角之间的差值，是由于地球轨道偏心引起的。C
     */

    public double solarEquationOfCenter(double M, double e) {
        double M_rad = M * DEG_TO_RAD;

        double e2 = e * e, e3 = e2 * e, e4 = e3 * e, e5 = e4 * e;
        double e6 = e5 * e, e7 = e6 * e, e8 = e7 * e;

        double C = (2 * e - e3 / 4.0 + 5 * e5 / 96.0 - 107 * e7 / 46080.0) * Math.sin(M_rad)
                + (5 * e2 / 4.0 - 11 * e4 / 24.0 + 17 * e6 / 192.0 - 181 * e8 / 32256.0) * Math.sin(2 * M_rad)
                + (13 * e3 / 12.0 - 43 * e5 / 64.0 + 95 * e7 / 576.0) * Math.sin(3 * M_rad)
                + (103 * e4 / 96.0 - 451 * e6 / 480.0 + 1237 * e8 / 5376.0) * Math.sin(4 * M_rad)
                + (1097 * e5 / 960.0 - 5957 * e7 / 23040.0) * Math.sin(5 * M_rad)
                + (1223 * e6 / 960.0 - 1381 * e8 / 4480.0) * Math.sin(6 * M_rad)
                + (47273 * e7 / 322560.0) * Math.sin(7 * M_rad)
                + (556403 * e8 / 2903040.0) * Math.sin(8 * M_rad);

        return C * RAD_TO_DEG;
    }

    /**
     * 平黄赤交角
     */
    public double meanObliquity(double T) {
        // IAU 2006公式（角秒）
        double epsilon_arcsec = 84381.406
                - 46.836769 * T
                - 0.0001831 * T * T
                + 0.00200340 * T * T * T
                - 0.000000576 * T * T * T * T
                - 0.0000000434 * T * T * T * T * T;

        // 转换为度
        return epsilon_arcsec / 3600.0;
    }

    /**
     * 太阳赤经
     */
    public double sunRightAscension(double lambda, double beta, double epsilon) {
        double lambda_rad = lambda * DEG_TO_RAD;
        double epsilon_rad = epsilon * DEG_TO_RAD;
        double beta_rad = beta * DEG_TO_RAD;

        // 球面三角公式
        double y = Math.sin(lambda_rad) * Math.cos(epsilon_rad)
                - Math.tan(beta_rad) * Math.sin(epsilon_rad);
        double x = Math.cos(lambda_rad);

        return Math.atan2(y, x) * RAD_TO_DEG;
    }


    /**
     * 章动,地球自转轴摆动
     */
    private double[] nutation(double T) {
        // 日月平角距
        double D = normalizeDegrees(297.85036 + T * (445267.111480 +
                T * (-0.0019142 + T / 189474.0)));
        // 太阳平近点角
        double M = meanSolarAnomaly(T);
        // 月亮平近点角
        double M_prime = normalizeDegrees(134.96298 + T * (477198.867398 +
                T * (0.0086972 + T / 56250.0)));
        // 月亮平黄经减去月亮升交点黄经
        double F = normalizeDegrees(93.27191 + T * (483202.017538 +
                T * (-0.0036825 + T / 327270.0)));
        // 月亮升交点平黄经
        double Omega = normalizeDegrees(125.04452 + T * (-1934.136261 +
                T * (0.0020708 + T / 450000.0)));

        double Omega_rad = Omega * DEG_TO_RAD;
        double F_rad = F * DEG_TO_RAD;
        double D_rad = D * DEG_TO_RAD;
        double M_rad = M * DEG_TO_RAD;
        double M_prime_rad = M_prime * DEG_TO_RAD;

        // 完整的章动项（10项主要项）
        double deltaPsi = -17.1996 * Math.sin(Omega_rad)
                + 0.2062 * Math.sin(2 * Omega_rad)
                - 1.3187 * Math.sin(2 * F_rad - 2 * D_rad + 2 * Omega_rad)
                - 0.2274 * Math.sin(2 * F_rad + 2 * Omega_rad)
                + 0.2070 * Math.sin(M_rad)
                + 0.1426 * Math.sin(M_prime_rad)
                - 0.0517 * Math.sin(2 * F_rad - 2 * D_rad + M_rad)
                + 0.0712 * Math.sin(2 * D_rad)
                - 0.0307 * Math.sin(2 * F_rad + 2 * D_rad + 2 * Omega_rad)
                + 0.0224 * Math.sin(2 * F_rad - 2 * D_rad + 2 * Omega_rad + M_rad);

        double deltaEpsilon = 9.2025 * Math.cos(Omega_rad)
                + 0.0895 * Math.cos(2 * Omega_rad)
                + 0.5736 * Math.cos(2 * F_rad - 2 * D_rad + 2 * Omega_rad)
                + 0.0977 * Math.cos(2 * F_rad + 2 * Omega_rad)
                + 0.0900 * Math.cos(M_rad)
                + 0.0549 * Math.cos(M_prime_rad)
                + 0.0279 * Math.cos(2 * F_rad - 2 * D_rad + M_rad)
                + 0.0154 * Math.cos(2 * D_rad)
                + 0.0128 * Math.cos(2 * F_rad + 2 * D_rad + 2 * Omega_rad)
                + 0.0075 * Math.cos(2 * F_rad - 2 * D_rad + 2 * Omega_rad + M_rad);

        return new double[]{deltaPsi * ARC_SEC_TO_DEG, deltaEpsilon * ARC_SEC_TO_DEG};
    }

    /**
     * 修正的均时差计算
     */
    public double[] equationOfTimeWithComponents(double T) {
        // 1. 基本轨道参数
        double L0 = meanSolarLongitude(T);
        double M = meanSolarAnomaly(T);
        double e = earthOrbitEccentricity(T);
        // 章动
        double[] nutation = nutation(T);
        double deltaPsi = nutation[0];
        double deltaEpsilon = nutation[1];
        //光行差修正
        double R = solarEarthDistance(M, e);
        double aberrationCorrection = LIGHT_TIME_ABERRATION / R;
        // 太阳真黄经（包含中心差+黄经章动（deltaPsi）+光行差）
        double C = solarEquationOfCenter(M, e);
        double lambda = L0 + C + deltaPsi + aberrationCorrection;

        // 3. 黄赤交角(含交角章动（deltaEpsilon）)
        double meanObliquity = meanObliquity(T);
        double epsilon = meanObliquity + deltaEpsilon;

        // 4. 使用修正后的真黄经和黄赤交角计算太阳的赤经
        double alpha = sunRightAscension(lambda, 0, epsilon);

        // 地球轨道偏心率导致的地球公转速度不均匀（速度分量）
        double E_eccentricity = -C;
        // 黄赤交角导致的太阳赤经变化率不均匀（倾斜分量）
        double E_obliquity = lambda - alpha;

        // 总均时差（考虑章动影响）
        double E_total = L0 - 0.0057183 - alpha + deltaPsi * Math.cos(Math.toRadians(epsilon));

        E_eccentricity = normalizeDegreesTo180(E_eccentricity);
        E_obliquity = normalizeDegreesTo180(E_obliquity);
        E_total = normalizeDegreesTo180(E_total);

        return new double[]{E_total, E_eccentricity, E_obliquity};
    }

    /**
     * 添加实用方法：转换为分钟
     */
    public double equationOfTimeToMinutes(double eotDegrees) {
        return eotDegrees * 4.0; // 1度 = 4分钟
    }

    /**
     * 直接计算均时差（分钟）
     */
    public double equationOfTimeMinutes(ZonedDateTime utc) {
        double T = uct2JulianCentury(utc);
        double[] eotComponents = equationOfTimeWithComponents(T);
        return equationOfTimeToMinutes(eotComponents[0]);
    }


    private double normalizeDegrees(double degrees) {
        degrees = degrees % 360;
        return degrees < 0 ? degrees + 360 : degrees;
    }

    private int INT(double x) {
        return (int) Math.floor(x);
    }
    private double normalizeDegreesTo180(double degrees) {
        degrees %= 360;
        if (degrees > 180) degrees -= 360;
        if (degrees < -180) degrees += 360;
        return degrees;
    }
}