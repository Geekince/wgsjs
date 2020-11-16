package com.wwsl.wgsj.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class MadelBean {
    private int id;
    private String name;
    private int enableUrl;
    private int disableUrl;
    private boolean enable;
    private String url;
}
