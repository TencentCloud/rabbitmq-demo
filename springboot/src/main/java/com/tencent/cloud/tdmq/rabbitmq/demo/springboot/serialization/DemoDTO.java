package com.tencent.cloud.tdmq.rabbitmq.demo.springboot.serialization;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DemoDTO implements Serializable {

    private String field;

    private String anotherField;

    private String yetAnotherField;
}
