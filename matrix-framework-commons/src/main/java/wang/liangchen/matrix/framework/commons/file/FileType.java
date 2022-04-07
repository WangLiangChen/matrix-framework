package wang.liangchen.matrix.framework.commons.file;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author LiangChen.Wang
 */
public enum FileType {
    NONE,
    ZIP, RAR, _7Z, TAR, GZ, TAR_GZ, BZ2, TAR_BZ2,
    BMP, PNG, JPG, JPEG, SVG,
    AVI, MP4, MP3, AAR, OGG, WAV, WAVE;

    public static FileType valueOf(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] head = new byte[4];
            if (-1 == inputStream.read(head)) {
                return FileType.NONE;
            }
            int headHex = 0;
            for (byte b : head) {
                headHex <<= 8;
                headHex |= b;
            }
            switch (headHex) {
                case 0x504B0304:
                    return FileType.ZIP;
                case 0x776f7264:
                    return FileType.TAR;
                case -0x51:
                    return FileType._7Z;
                case 0x425a6839:
                    return FileType.BZ2;
                case -0x74f7f8:
                    return FileType.GZ;
                case 0x52617221:
                    return FileType.RAR;
                default:
                    return FileType.NONE;
            }
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
