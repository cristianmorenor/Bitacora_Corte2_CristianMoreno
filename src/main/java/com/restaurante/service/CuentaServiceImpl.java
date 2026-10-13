package com.restaurante.service;

import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.CuentaPersistenceMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.repository.CuentaRepository;
import com.restaurante.repository.MesaRepository;
import com.restaurante.validator.CuentaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MesaRepository mesaRepository;
    private final CuentaPersistenceMapper entityMapper;
    private final CuentaValidator cuentaValidator;

    @Override
    @Transactional
    public Cuenta abrirCuenta(Long idMesa) {
        MesaEntity mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new MesaNotFoundException("Mesa no encontrada con id: " + idMesa));

        cuentaValidator.validarMesaSinCuentaAbierta(idMesa);

        Cuenta cuenta = new Cuenta();
        cuenta.setTotal(0.0);
        cuenta.setEstado(EstadoCuenta.ABIERTA);
        cuenta.setApertura(LocalDateTime.now());

        CuentaEntity entity = entityMapper.toEntity(cuenta);
        entity.setMesa(mesa);

        Cuenta abierta = entityMapper.toDomain(cuentaRepository.save(entity));
        log.info("Cuenta id={} abierta para mesa id={}", abierta.getId(), idMesa);
        return abierta;
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta obtenerPorId(Long id) {
        return entityMapper.toDomain(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> obtenerCuentasAbiertasPorMesa(Long idMesa) {
        return cuentaRepository.findByMesaIdAndEstado(idMesa, EstadoCuenta.ABIERTA).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Cuenta agregarMonto(Long id, Double monto) {
        CuentaEntity entity = buscarEntidad(id);
        Cuenta cuenta = entityMapper.toDomain(entity);

        cuentaValidator.validarCuentaAbierta(cuenta, "La cuenta " + id + " no esta abierta, no se pueden agregar montos");

        cuenta.agregarMonto(monto);
        entity.setTotal(cuenta.getTotal());
        cuentaRepository.save(entity);
        log.info("Monto {} agregado a cuenta id={}, nuevo total={}", monto, id, cuenta.getTotal());
        return cuenta;
    }

    @Override
    @Transactional
    public Cuenta registrarPago(Long id, Double monto) {
        CuentaEntity entity = buscarEntidad(id);
        Cuenta cuenta = entityMapper.toDomain(entity);

        cuentaValidator.validarCuentaAbierta(cuenta, "La cuenta " + id + " ya fue pagada o cerrada");
        cuentaValidator.validarMontoCubreTotal(cuenta, monto);

        cuenta.registrarPago();
        cuenta.cerrarCuenta();
        entity.setEstado(cuenta.getEstado());
        cuentaRepository.save(entity);
        log.info("Pago de {} registrado en cuenta id={}, cuenta cerrada", monto, id);
        return cuenta;
    }

    private CuentaEntity buscarEntidad(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + id));
    }
}
