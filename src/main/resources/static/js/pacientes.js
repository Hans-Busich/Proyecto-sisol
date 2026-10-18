'use strict';
(() => {
    const app = document.getElementById('pacientes-app');
    const api = app.dataset.api;
    const rows = document.getElementById('filas');
    const modal = document.getElementById('modal-paciente');
    const deleteModal = document.getElementById('modal-eliminar');
    const form = document.getElementById('form-paciente');
    const save = document.getElementById('guardar');
    const fields = ['dni', 'nombres', 'apellidos', 'fechaNacimiento', 'telefono', 'email'];
    let editingId = null;
    let deletingId = null;
    let busy = false;

    function message(element, text, error = true) {
        element.textContent = text;
        element.hidden = !text;
        element.className = 'mensaje ' + (error ? 'error' : 'exito');
    }
    function setBusy(value) {
        busy = value;
        document.querySelectorAll('button').forEach(button => { button.disabled = value; });
        form.querySelectorAll('input').forEach(input => { input.disabled = value; });
        modal.setAttribute('aria-busy', String(value));
        deleteModal.setAttribute('aria-busy', String(value));
    }
    async function request(path = '', options = {}) {
        let response;
        try {
            response = await fetch(api + path, { credentials: 'same-origin', ...options });
        } catch {
            throw { mensaje: 'No se pudo confirmar la operación. Actualiza el listado antes de volver a intentarlo.' };
        }
        if (response.status === 204) return null;
        let data;
        try { data = await response.json(); }
        catch { throw { mensaje: 'El servidor devolvió una respuesta inesperada. Actualiza el listado.' }; }
        if (!response.ok) throw data;
        return data;
    }
    function updateCount() {
        const count = rows.children.length;
        document.getElementById('cantidad').textContent = `${count} registros`;
        document.getElementById('vacio').hidden = count !== 0;
    }
    function makeRow(patient) {
        const row = document.createElement('tr');
        row.dataset.id = patient.idPaciente;
        [patient.idPaciente, patient.dni, `${patient.nombres} ${patient.apellidos}`, patient.fechaNacimiento].forEach(value => {
            const cell = row.insertCell(); cell.textContent = value ?? '';
        });
        const contact = row.insertCell();
        contact.append(document.createTextNode(patient.telefono ?? ''), document.createElement('br'), document.createTextNode(patient.email ?? ''));
        const actions = document.createElement('div'); actions.className = 'acciones';
        ['editar', 'eliminar'].forEach(action => {
            const button = document.createElement('button');
            button.type = 'button'; button.className = action === 'editar' ? 'secundario' : 'peligro';
            button.textContent = action === 'editar' ? 'Editar' : 'Eliminar';
            button.dataset.accion = action; button.dataset.id = patient.idPaciente;
            button.dataset.nombre = `${patient.nombres} ${patient.apellidos}`;
            actions.append(button);
        });
        row.insertCell().append(actions);
        return row;
    }
    function clearErrors() {
        message(document.getElementById('error-formulario'), '');
        fields.forEach(field => {
            message(document.getElementById('error-' + field), '');
            document.getElementById(field).removeAttribute('aria-invalid');
        });
    }
    function openForm(patient = null) {
        form.reset(); clearErrors();
        editingId = patient?.idPaciente ?? null;
        fields.forEach(field => { document.getElementById(field).value = patient?.[field] ?? ''; });
        document.getElementById('titulo-modal').textContent = editingId ? 'Editar paciente' : 'Agregar paciente';
        save.textContent = editingId ? 'Guardar cambios' : 'Agregar paciente';
        modal.showModal();
        document.getElementById('dni').focus();
    }
    document.getElementById('agregar').addEventListener('click', () => openForm());
    document.querySelectorAll('[data-cerrar]').forEach(button => button.addEventListener('click', () => { if (!busy) modal.close(); }));
    [modal, deleteModal].forEach(dialog => dialog.addEventListener('cancel', event => { if (busy) event.preventDefault(); }));

    async function openEdit(id) {
        setBusy(true);
        try { const patient = await request('/' + id); setBusy(false); openForm(patient); }
        catch (error) { message(document.getElementById('mensaje'), error.mensaje || 'No se pudo cargar el paciente.'); }
        finally { setBusy(false); }
    }
    rows.addEventListener('click', event => {
        const button = event.target.closest('button[data-accion]');
        if (!button || busy) return;
        if (button.dataset.accion === 'editar') { openEdit(button.dataset.id); return; }
        deletingId = button.dataset.id;
        document.getElementById('detalle-eliminar').textContent = `¿Eliminar a ${button.dataset.nombre}?`;
        message(document.getElementById('error-eliminar'), '');
        deleteModal.showModal();
        document.getElementById('cancelar-eliminar').focus();
    });
    form.addEventListener('submit', async event => {
        event.preventDefault();
        if (busy || !form.reportValidity()) return;
        clearErrors();
        const body = Object.fromEntries(fields.map(field => [field, document.getElementById(field).value.trim() || null]));
        const isEdit = editingId !== null;
        setBusy(true);
        try {
            const patient = await request(isEdit ? '/' + editingId : '', {
                method: isEdit ? 'PUT' : 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
            });
            const row = makeRow(patient);
            const previous = rows.querySelector(`tr[data-id="${patient.idPaciente}"]`);
            if (previous) previous.replaceWith(row); else rows.append(row);
            updateCount(); modal.close();
            message(document.getElementById('mensaje'), isEdit ? 'Paciente actualizado correctamente.' : 'Paciente agregado correctamente.', false);
        } catch (error) {
            message(document.getElementById('error-formulario'), error.mensaje || 'No se pudo guardar el paciente.');
            let first = null;
            Object.entries(error.errores || {}).forEach(([field, text]) => {
                if (!fields.includes(field)) return;
                const element = document.getElementById('error-' + field);
                element.textContent = text; element.hidden = false; element.className = 'campo-error';
                document.getElementById(field).setAttribute('aria-invalid', 'true');
                first ??= document.getElementById(field);
            });
            setBusy(false); first?.focus();
        } finally { setBusy(false); }
    });
    document.getElementById('cancelar-eliminar').addEventListener('click', () => { if (!busy) deleteModal.close(); });
    document.getElementById('confirmar-eliminar').addEventListener('click', async () => {
        if (busy || deletingId === null) return;
        setBusy(true);
        try {
            await request('/' + deletingId, { method: 'DELETE' });
            rows.querySelector(`tr[data-id="${deletingId}"]`)?.remove(); updateCount(); deleteModal.close();
            message(document.getElementById('mensaje'), 'Paciente eliminado correctamente.', false);
        } catch (error) { message(document.getElementById('error-eliminar'), error.mensaje || 'No se pudo eliminar el paciente.'); }
        finally { setBusy(false); }
    });
    document.getElementById('actualizar').addEventListener('click', async () => {
        if (busy) return;
        setBusy(true);
        try {
            const patients = await request(); rows.replaceChildren(...patients.map(makeRow)); updateCount();
            document.getElementById('error-consulta')?.remove(); message(document.getElementById('mensaje'), 'Listado actualizado.', false);
        } catch (error) { message(document.getElementById('mensaje'), error.mensaje || 'No se pudo actualizar el listado.'); }
        finally { setBusy(false); }
    });
    if (app.dataset.idEdicion) openEdit(app.dataset.idEdicion);
})();
