package com.ticket12306.android.data.repository

import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.ticket12306.android.data.model.*
import com.ticket12306.android.data.remote.api.OrderApi
import com.ticket12306.android.data.remote.api.TicketApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TicketRepository(
    private val ticketApi: TicketApi,
    private val orderApi: OrderApi,
    private val database: AppDatabase,
    private val userPreferences: UserPreferences
) {

    val activeBookingTasks: Flow<List<BookingTask>> = database.bookingTaskDao().getActiveBookingTasks()
    val allBookingTasks: Flow<List<BookingTask>> = database.bookingTaskDao().getAllBookingTasks()

    suspend fun queryTickets(
        fromStation: String,
        toStation: String,
        date: String
    ): Result<List<TicketInfo>> {
        return try {
            val response = ticketApi.queryTickets(
                trainDate = date,
                fromStation = fromStation,
                toStation = toStation
            )

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                val tickets = response.body()!!.data?.result ?: emptyList()
                val ticketInfos = tickets.map { parseTicketInfo(it) }
                Result.success(ticketInfos)
            } else {
                Result.failure(Exception(response.body()?.result_message ?: "查询失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseTicketInfo(item: TicketResultItem): TicketInfo {
        return TicketInfo(
            trainCode = item.trainCode,
            trainNo = item.trainNo,
            startStation = item.startStationTelecode,
            endStation = item.endStationTelecode,
            fromStation = item.fromStationTelecode,
            toStation = item.toStationTelecode,
            startTime = item.startTime,
            arriveTime = item.arriveTime,
            dayDifference = item.dayDifference,
            trainClassName = item.trainClassName,
            duration = item.duration,
            canWebBuy = item.canWebBuy,
            seatTypes = item.seatPrices.map { (key, value) ->
                key to SeatInfo(
                    seatType = value.seatType,
                    seatTypeName = value.seatTypeName,
                    price = value.price,
                    remainTicket = value.remainCount,
                    canBuy = value.canBuy
                )
            }.toMap()
        )
    }

    suspend fun submitOrder(ticket: TicketInfo, date: String): Result<String> {
        return try {
            val request = OrderRequest(
                secretStr = ticket.trainNo,
                train_date = date,
                back_train_date = date,
                query_from_station_name = ticket.fromStation,
                query_to_station_name = ticket.toStation
            )

            val response = orderApi.submitOrderRequest(request)

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data ?: "")
            } else {
                Result.failure(Exception(response.body()?.result_message ?: "提交订单失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmOrder(
        ticket: TicketInfo,
        passengers: List<Passenger>,
        seatType: String
    ): Result<OrderResult> {
        return try {
            val passengerTicketStr = passengers.joinToString("") { passenger ->
                "${seatType},0,1,${passenger.passenger_name},1,${passenger.passenger_id_no},,N"
            }

            val oldPassengerStr = passengers.joinToString("") { passenger ->
                "${passenger.passenger_name},1,${passenger.passenger_id_type_code},${passenger.passenger_id_no},1_"
            }

            val request = ConfirmOrderRequest(
                passengerTicketStr = passengerTicketStr,
                oldPassengerStr = oldPassengerStr,
                randCode = "",
                key_check_isChange = "",
                leftTicketStr = "",
                train_location = ""
            )

            val response = orderApi.confirmSingleForQueue(request)

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.result_message ?: "确认订单失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBookingTask(task: BookingTask): Result<Long> {
        return try {
            val id = database.bookingTaskDao().insertBookingTask(task)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookingTask(task: BookingTask): Result<Unit> {
        return try {
            database.bookingTaskDao().updateBookingTask(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBookingTask(taskId: Long): Result<Unit> {
        return try {
            database.bookingTaskDao().deleteBookingTaskById(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateBookingTask(taskId: Long): Result<Unit> {
        return try {
            database.bookingTaskDao().activateBookingTask(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deactivateBookingTask(taskId: Long): Result<Unit> {
        return try {
            database.bookingTaskDao().deactivateBookingTask(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveBookingTasks(): List<BookingTask> {
        return database.bookingTaskDao().getActiveBookingTasks().first()
    }

    suspend fun checkTicketAvailability(task: BookingTask): TicketCheckResult {
        val result = queryTickets(
            fromStation = task.departureStation,
            toStation = task.arrivalStation,
            date = task.departureDate
        )

        return result.fold(
            onSuccess = { tickets ->
                val targetTicket = tickets.find { it.trainCode == task.trainNumber }
                if (targetTicket != null) {
                    val seatInfo = targetTicket.seatTypes[task.seatType]
                    if (seatInfo != null && seatInfo.remainTicket > 0) {
                        TicketCheckResult(
                            hasTicket = true,
                            ticketInfo = targetTicket,
                            seatInfo = seatInfo
                        )
                    } else {
                        TicketCheckResult(hasTicket = false)
                    }
                } else {
                    TicketCheckResult(hasTicket = false)
                }
            },
            onFailure = {
                TicketCheckResult(hasTicket = false, error = it.message)
            }
        )
    }

    suspend fun bookTicket(task: BookingTask): BookingResult {
        val checkResult = checkTicketAvailability(task)

        if (!checkResult.hasTicket) {
            return BookingResult(
                success = false,
                errorMessage = checkResult.error ?: "无票"
            )
        }

        val passengers = database.passengerDao().getPassengersByCodes(task.passengerIds)

        if (passengers.isEmpty()) {
            return BookingResult(
                success = false,
                errorMessage = "未找到乘客信息"
            )
        }

        val orderResult = confirmOrder(
            ticket = checkResult.ticketInfo!!,
            passengers = passengers,
            seatType = task.seatType
        )

        return orderResult.fold(
            onSuccess = { result ->
                if (result.submitStatus) {
                    BookingResult(
                        success = true,
                        orderSequence = result.orderSequence
                    )
                } else {
                    BookingResult(
                        success = false,
                        errorMessage = result.errorMessage ?: "预订失败"
                    )
                }
            },
            onFailure = { error ->
                BookingResult(
                    success = false,
                    errorMessage = error.message ?: "预订失败"
                )
            }
        )
    }

    suspend fun queryMyOrders(startDate: String, endDate: String): Result<List<OrderInfo>> {
        return try {
            val response = orderApi.queryMyOrder(
                startDate = startDate,
                endDate = endDate
            )

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                val orders = response.body()!!.data?.orderDTODataList ?: emptyList()
                Result.success(orders)
            } else {
                Result.failure(Exception(response.body()?.result_message ?: "查询订单失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 查询未完成订单（待支付）
     * 步骤：调用OrderApi获取未支付订单列表
     */
    suspend fun queryUnpaidOrders(): Result<List<OrderInfo>> {
        return try {
            val response = orderApi.queryUnpaidOrder()
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                val orders = response.body()!!.data?.orderDTODataList ?: emptyList()
                Result.success(orders)
            } else {
                Result.failure(Exception(response.body()?.result_message ?: "查询未支付订单失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 取消订单
     * 步骤：
     * 1. 构建CancelOrderRequest
     * 2. 调用OrderApi取消订单
     * 3. 返回取消结果
     */
    suspend fun cancelOrder(sequenceNo: String): Result<Boolean> {
        return try {
            val request = CancelOrderRequest(sequence_no = sequenceNo)
            val response = orderApi.cancelOrder(request)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                val cancelStatus = response.body()!!.data?.cancelStatus ?: false
                Result.success(cancelStatus)
            } else {
                Result.failure(Exception(response.body()?.data?.errMsg ?: "取消订单失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
