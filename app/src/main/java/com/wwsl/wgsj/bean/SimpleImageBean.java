package com.wwsl.wgsj.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleImageBean {
    private String url;
    private boolean locked;
}
