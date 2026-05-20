/**
 * group-strategy.model.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Union type mirroring the Java GroupStrategy enum —
 *              defines all supported aggregation dimensions for time-tracking queries.
 */

export type GroupStrategy = 'NONE' | 'PROJECT' | 'PROJECT_EMPLOYEE' | 'EMPLOYEE_PROJECT';

export const GROUP_STRATEGY_OPTIONS: GroupStrategy[] = [
  'NONE',
  'PROJECT',
  'PROJECT_EMPLOYEE',
  'EMPLOYEE_PROJECT',
];