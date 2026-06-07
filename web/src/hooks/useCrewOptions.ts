/* ========================================
   班组候选人 Hook
   司机 = 运输部(TRANSPORT) 用户；跟车员 = 仓库(WAREHOUSE)+生产(PRODUCTION) 用户。
   供车辆/打卡表单的“选人”下拉使用。
   ======================================== */
import { useEffect, useState } from 'react';
import { userApi } from '@/api/user';

export interface StaffOption {
  label: string;
  value: number;
}

export function useCrewOptions() {
  const [driverOptions, setDriverOptions] = useState<StaffOption[]>([]);
  const [escortOptions, setEscortOptions] = useState<StaffOption[]>([]);

  useEffect(() => {
    userApi
      .list({ departmentType: 'TRANSPORT', status: 1, pageSize: 100 })
      .then((res) =>
        setDriverOptions(
          res.data.items.map((u) => ({ label: u.jobTitle ? `${u.name}（${u.jobTitle}）` : u.name, value: u.id })),
        ),
      )
      .catch(() => {});

    Promise.all([
      userApi.list({ departmentType: 'WAREHOUSE', status: 1, pageSize: 100 }),
      userApi.list({ departmentType: 'PRODUCTION', status: 1, pageSize: 100 }),
    ])
      .then(([wh, prod]) =>
        setEscortOptions(
          [...wh.data.items, ...prod.data.items].map((u) => ({
            label: u.departmentName ? `${u.name}（${u.departmentName}）` : u.name,
            value: u.id,
          })),
        ),
      )
      .catch(() => {});
  }, []);

  return { driverOptions, escortOptions };
}
