import {getAnalysisByUserId, getCashDistributionByUserId} from "@/api/analysis.js"


/**
 * 获取指定用户ID的统计信息
 */


/**
 * 获取所有礼簿的金额分布
 * 饼图
 */
export function CashDistributionFigure(data) {
    // 将数据转换为 ECharts 饼图所需的格式
    const formattedData = data.map(item => ({
        value: item.total,
        name: `${item.cash}元`
    }));
    return {
        title: {
            text: '所有礼簿的金额分布',
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            // bottom: 'bottom',
            left :'left'
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                radius: '50%',
                data: formattedData,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

}