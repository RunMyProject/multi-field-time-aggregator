/**
 * employee-project-hours-row.model.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Projection DTO representing aggregated hours bucketed by employee → project,
 *              mirroring the Java EmployeeProjectHoursRow record.
 *              Returned by the backend when groupBy=EMPLOYEE_PROJECT.
 */

export interface EmployeeProjectHoursRow {
  employeeName: string;
  projectName: string;
  totalHours: number;
}