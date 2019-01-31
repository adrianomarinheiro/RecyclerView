package br.com.adrianomarinheiro.recyclerview.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import br.com.adrianomarinheiro.recyclerview.R
import br.com.adrianomarinheiro.recyclerview.api.getPokemonAPI
import br.com.adrianomarinheiro.recyclerview.model.Pokemon
import br.com.adrianomarinheiro.recyclerview.model.PokemonResponse
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lista.*

class ListaActivity : AppCompatActivity() {

    private var disposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        carregarDados()
    }

    private fun exibeNaLista(pokemons: List<Pokemon>){
        rvPokemons.adapter = ListaPokemonAdapter(this, pokemons, {
            Toast.makeText(this, it.nome, Toast.LENGTH_LONG).show()
        })
        rvPokemons.layoutManager = LinearLayoutManager(this)
    }

    private fun exibeErro(erro: String){
        Toast.makeText(this, erro, Toast.LENGTH_LONG).show()
    }

    private fun carregarDados(){
        getPokemonAPI()
                .buscar(150)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<PokemonResponse> {
                    override fun onComplete() {
                        Log.i("ListaActivity", "COMPLETE")
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: PokemonResponse) {
                        exibeNaLista(t.pokemons)
                    }

                    override fun onError(e: Throwable) {
                        exibeErro(e.message!!)
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
