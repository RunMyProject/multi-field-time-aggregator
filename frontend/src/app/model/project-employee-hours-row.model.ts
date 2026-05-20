/**
 * project-employee-hours-row.model.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Projection DTO representing aggregated hours bucketed by project → employee,
 *              mirroring the Java ProjectEmployeeHoursRow record.
 *              Returned by the backend when groupBy=PROJECT_EMPLOYEE.
 */

export interface ProjectEmployeeHoursRow {
  projectName: string;
  employeeName: string;
  totalHours: number;
}