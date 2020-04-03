import request from '@/utils/request'

// 查询商户列表
export function listMchInfo(query) {
  return request({
    url: '/pay/mchinfo/list',
    method: 'get',
    params: query
  })
}

// 查询商户详细
export function getMchInfo(mchId) {
  return request({
    url: '/pay/mchinfo/' + mchId,
    method: 'get'
  })
}

// 新增商户
export function addMchInfo(data) {
  return request({
    url: '/pay/mchinfo',
    method: 'post',
    data: data
  })
}

// 修改商户
export function updateMchInfo(data) {
  return request({
    url: '/pay/mchinfo',
    method: 'put',
    data: data
  })
}

// 删除商户
export function delMchInfo(mchId) {
  return request({
    url: '/pay/mchinfo/' + mchId,
    method: 'delete'
  })
}

// 导出岗位
export function exportMchInfo(query) {
  return request({
    url: '/pay/mchinfo/export',
    method: 'get',
    params: query
  })
}
