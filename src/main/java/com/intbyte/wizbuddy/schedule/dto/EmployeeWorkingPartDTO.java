package com.intbyte.wizbuddy.schedule.dto;

import com.intbyte.wizbuddy.schedule.domain.entity.WeeklySchedule;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter @Setter
public class EmployeeWorkingPartDTO {

    private int workingPartCode;

    private int employeeCode;

    private int scheduleCode;

    private LocalDateTime workingDate;

    private String workingPartTime;

}