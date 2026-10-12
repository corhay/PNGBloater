package bloater;

import java.util.List;

import img.Image;

public interface ImageBloater<I extends Image<?>> {
    I bloat(I img, int size);
    I debloat(I img);
}
