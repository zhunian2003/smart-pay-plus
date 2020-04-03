import request from '@/utils/request'

// 查询渠道列表
export function listChannel(query) {
  return request({
    url: '/pay/paychannel/list',
    method: 'get',
    params: query
  })
}

// 查询渠道详细
export function getChannel(mchId) {
  return request({
    url: '/pay/paychannel/' + mchId,
    method: 'get'
  })
}

// 新增渠道
export function addChannel(data) {
  return request({
    url: '/pay/paychannel',
    method: 'post',
    data: data
  })
}

// 修改渠道
export function updateChannel(data) {
  return request({
    url: '/pay/paychannel',
    method: 'put',
    data: data
  })
}

// 删除渠道
export function delChannel(mchId) {
  return request({
    url: '/pay/paychannel/' + mchId,
    method: 'delete'
  })
}

// 导出渠道
export function exportChannel(query) {
  return request({
    url: '/pay/paychannel/export',
    method: 'get',
    params: query
  })
}
