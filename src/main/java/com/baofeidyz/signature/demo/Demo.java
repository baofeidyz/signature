package com.baofeidyz.signature.demo;

import com.baofeidyz.signature.pojo.dto.SignatureDTO;
import java.io.IOException;
import java.io.InputStream;

public interface Demo {

    InputStream createImage(SignatureDTO signatureDTO) throws IOException;

}
