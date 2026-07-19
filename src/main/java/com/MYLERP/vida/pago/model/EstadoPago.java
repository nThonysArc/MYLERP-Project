package com.MYLERP.vida.pago.model;

/**
 * Solo estados que el usuario controla manualmente.
 * "VENCIDO" NO vive aquí: se calcula en la vista vida.pagos_con_estado_real
 * comparando fecha_vencimiento contra CURRENT_DATE, para no tener que
 * sincronizar ese estado con un job constantemente.
 */
public enum EstadoPago {
    PENDIENTE,
    PAGADO
}
