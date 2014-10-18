#pragma version(1)
#pragma rs_fp_inprecise
#pragma rs java_package_name(com.felipecsl.gifimageview.library)

rs_allocation image;
rs_allocation pixels;  // pixels array for whole image
rs_allocation colors;  // current color table

uint32_t width;
uint32_t height;

uint32_t fx, fy, fw, fh; // current frame dimensions
uchar4 bgColor;

uint32_t radius;

typedef uint8_t byte;

static const uint32_t MAX_STACK_SIZE = 4096;

void __attribute__((kernel)) clear(uint32_t line) { // clear a line (but only in current frame)
    if (line >= fy && line < fy + fh) {
        int end = fx + fw;
        for (int i = fx; i < end; ++i) {
            rsSetElementAt_uchar4(pixels, bgColor, i, line);
        }
    }
}

void __attribute__((kernel)) setColor(uint32_t line) { // clear a line (but only in current frame)
    if (line >= fy && line < fy + fh) {
        int end = fx + fw;
        for (int i = fx; i < end; ++i) {
            rsSetElementAt_uchar4(pixels, bgColor, i, line);
        }
    }
}

void __attribute__((kernel)) decode(uint32_t in) {
    int idx = 0;
    int nullCode = -1;
    int npix = width * height;
    int available, clear, code_mask, code_size, end_of_information, in_code, old_code, bits, code, count, i, datum, data_size, first, top, bi, pi;
    uchar4 color = 0;

    short prefix[MAX_STACK_SIZE];
    uint8_t suffix[MAX_STACK_SIZE];
    uint8_t pixelStack[MAX_STACK_SIZE + 1];
    uint8_t block[256];

    // Initialize GIF data stream decoder.
    data_size = rsGetElementAt_uchar(image, idx++);
    clear = 1 << data_size;
    end_of_information = clear + 1;
    available = clear + 2;
    old_code = nullCode;
    code_size = data_size + 1;
    code_mask = (1 << code_size) - 1;
    for (code = 0; code < clear; code++) {
        prefix[code] = 0; // XXX ArrayIndexOutOfBoundsException
        suffix[code] = (byte)code;
    }

    // Decode GIF pixel stream.
    datum = bits = count = first = top = pi = bi = 0;
    int endY = fy + fh;
    int endX = fx + fw;
    int x = fx;
    int y = fy;
    for (i = 0; i < npix;) {
        if (top == 0) {
            if (bits < code_size) {
                // Load bytes until there are enough bits for a code.
                if (count == 0) {
                    // Read a new data block.
                    count = rsGetElementAt_uchar(image, idx++);
                    // TODO: bounds check, make sure allocation has enough data
                    for (int k = 0; k < count; ++k) {
                        block[k] = rsGetElementAt_uchar(image, idx++);
                    }

                    if (count <= 0) {
                        break;
                    }
                    bi = 0;
                }
                datum += ((block[bi]) & 0xff) << bits;
                bits += 8;
                bi++;
                count--;
                continue;
            }
            // Get the next code.
            code = datum & code_mask;
            datum >>= code_size;
            bits -= code_size;
            // Interpret the code
            if ((code > available) || (code == end_of_information)) {
                break;
            }
            if (code == clear) {
                // Reset decoder.
                code_size = data_size + 1;
                code_mask = (1 << code_size) - 1;
                available = clear + 2;
                old_code = nullCode;
                continue;
            }
            if (old_code == nullCode) {
                pixelStack[top++] = suffix[code];
                old_code = code;
                first = code;
                continue;
            }
            in_code = code;
            if (code == available) {
                pixelStack[top++] = (byte)first;
                code = old_code;
            }
            while (code > clear) {
                pixelStack[top++] = suffix[code];
                code = prefix[code];
            }
            first = (suffix[code]) & 0xff;
            // Add a new string to the string table,
            if (available >= MAX_STACK_SIZE) {
                break;
            }
            pixelStack[top++] = (byte)first;
            prefix[available] = (short)old_code;
            suffix[available] = (byte)first;
            available++;
            if (((available & code_mask) == 0) && (available < MAX_STACK_SIZE)) {
                code_size++;
                code_mask += available;
            }
            old_code = in_code;
        }
        // Pop a pixel off the pixel stack.
        top--;
        color = rsGetElementAt_uchar4(colors, pixelStack[top]).bgra;
        if (color.a != 0) {
            rsSetElementAt_uchar4(pixels, color, x, y);
        }
        ++i;
        ++x;
        if (x == endX) {
            x = fx;
            ++y;
        }
    }
    //for (i = pi; i < npix; i++) {
    //    rsSetElementAt_uchar(pixels, 0, i); // clear missing pixels
    //}
}
