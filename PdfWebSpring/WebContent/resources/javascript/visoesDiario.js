  


$(document).ready(function() {	
	
	$('.open-popup-link').magnificPopup({
		type:'inline',
		midClick: true // Allow opening popup on middle mouse click. Always set it to true if you don't provide alternative source in href.
	});
	
	$("#button").click(function() {
		$("#div").toggle();
		var src = $("#seta").attr('src');
		if(src == '/sioplegis-visoes/resources/imagens/seta_baixo_visoes_diario.png')
			$("#seta").attr('src', '/sioplegis-visoes/resources/imagens/seta_visoes_diario.png');
		else
			$("#seta").attr('src', '/sioplegis-visoes/resources/imagens/seta_baixo_visoes_diario.png');
	});
	
	
	
	$("#mostrar").click(function() {
		$("#completo").slideToggle("slow");
	});
	
	$("#busca").keyup(function (e) {

		if(e.keyCode == 13 && itemSelecionado){ 
			itemSelecionado = false;			
		}
		else if (e.keyCode == 13) $("#botaoPesquisa").click();						
		
		itemSelecionado = false;

		return false;
						    
	});
		
});

$(function(){
	
	    var pickerOpts = {	
	        dateFormat:"dd/mm/yy",
	        dayNamesMin: [ "Do", "Se", "Te", "Qu", "Qu", "Se", "Sa" ]	
	    };
	    
	    
	    
	    $(".datepicker").datepicker(pickerOpts);	    
		$( ".datepicker" ).datepicker( "option", "monthNamesShort", [ "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez" ] );
		$( ".datepicker" ).datepicker( "option", "changeMonth", true );
		$( ".datepicker" ).datepicker( "option", "changeYear", true );
});

function expandir(indice){
	var index = 'repeat:'+indice+':a';
	var index1 = 'repeat:'+indice+':a1';
	var expandirSimbolo = 'repeat:'+indice+':symbol';
	var expandirTexto = 'repeat:'+indice+':m';
	
	
	$("#repeat\\:" + indice + "\\:a span:first").toggleClass("invisivel");
	
	if(document.getElementById(index) != null && document.getElementById(index).style.maxHeight == "43px"){
		 document.getElementById(index).style.maxHeight="none";
		 document.getElementById(expandirSimbolo).innerHTML = "-";
		 document.getElementById(expandirTexto).innerHTML = "Ver menos";
	}
	else if(document.getElementById(index1) != null && document.getElementById(index1).style.maxHeight == "43px"){
		document.getElementById(index1).style.maxHeight="none";
		document.getElementById(expandirSimbolo).innerHTML = "-";
		document.getElementById(expandirTexto).innerHTML = "Ver menos";
	}
	else if(document.getElementById(index) != null && document.getElementById(index).style.maxHeight == "none"){
		document.getElementById(index).style.maxHeight="43px";
		document.getElementById(expandirSimbolo).innerHTML = "+";
		document.getElementById(expandirTexto).innerHTML = "Ver mais";
	}
	else if(document.getElementById(index1) != null && document.getElementById(index1).style.maxHeight == "none"){
		document.getElementById(index1).style.maxHeight="43px";
		document.getElementById(expandirSimbolo).innerHTML = "+";
		document.getElementById(expandirTexto).innerHTML = "Ver mais";
	}
	
}

var itemSelecionado = false;

$(function() {
  var campos = [
  {
        value: "tipo:()",
        label: "tipo:(<span class='interiorParenteses'>insira o conteúdo</span>)",
        desc: "Filtra os resultados pelo tipo de documento especificado entre parênteses."		            
  },
  {
        value: "corpo:()",
        label: "corpo:(<span class='interiorParenteses'>insira o conteúdo</span>)",
        desc: "Filtra os resultados pelo corpo de texto especificado entre parênteses."
  },
  {
        value: "dataAssinatura:()",
        label: "dataAssinatura:(<span class='interiorParenteses'>insira a data de assinatura no formato aaaammdd</span>)",
        desc: "Filtra os resultados pela data de assinatura especificada entre parênteses."
  },
  {
        value: "dataAssinatura:[]",
        label: "dataAssinatura:[<span class='interiorParenteses'>insira a data incial no formato aaaammdd</span> TO <span class='interiorParenteses'>insira a data final no formato aaaammdd</span>]",
        desc: "Filtra os resultados cuja data de assinatura se encontra no intervalo das datas especificadas entre colchetes."
  },
  {
        value: "dataPublicacao:()",
        label: "dataPublicacao:(<span class='interiorParenteses'>insira a data de publicão no formato aaaammdd</span>)",
        desc: "Filtra os resultados pela data de publicacão especificada entre parênteses."
  },
  {
        value: "dataPublicacao:[]",
        label: "dataPublicacao:[<span class='interiorParenteses'>insira a data incial no formato aaaammdd</span> TO <span class='interiorParenteses'>insira a data final no formato aaaammdd</span>]",
        desc: "Filtra os resultados cuja data de publicacão se encontra no intervalo das datas especificadas entre parênteses."
  },
  {
        value: "ementa:()",
        label: "ementa:(<span class='interiorParenteses'>insira o conteúdo</span>)",
        desc: "Filtra os resultados pela ementa casa especificada entre parênteses."
  },
  {
        value: "autoridade:()",
        label: "autoridade:(<span class='interiorParenteses'>insira o conteúdo</span>)",
        desc: "Filtra os resultados pelo orgão/autoridade especificado entre parênteses."
  },
  {
        value: "artigoPrimeiro:()",
        label: "artigoPrimeiro:(<span class='interiorParenteses'>insira o conteúdo</span>)",
        desc: "Filtra os resultados pelo artigo primeiro especificado entre parênteses."
  },
  {
        value: "descricao:()",
        label: "descricao:(<span class='interiorParenteses'>insira o conteúdo</span>)",
        desc: "Filtra os resultados pela descrição especificada entre parênteses."
  },
  {
        value: "secao:()",
        label: "secao:(<span class='interiorParenteses'>insira o valor 1 para seção 1, 2 para a seção 2, 3 para a seção 3 e 1000 para a seção extra</span>)",
        desc: "Filtra os resultados que foram publicados no DOU da seção especificada entre parênteses."
  },
  {
        value: "pagina:()",
        label: "pagina:(<span class='interiorParenteses'>insira a página</span>)",
        desc: "Filtra os resultados que foram publicados na página do DOU especificada entre parênteses."
  },
  {
      value: "numero:()",
      label: "numero:(<span class='interiorParenteses'>insira o número</span>)",
      desc: "Filtra os resultados pelo número especificado entre parênteses."
  }
  
];

 function split( val ) {
	return val.split(/\s+/);
 }

 function extractLast( term ) {
	var last = split( term ).pop();
	return last;
 }
 
 $( "#busca" ).autocomplete({
  minLength: 0,
  source: function( request, response ) {
      // delegate back to autocomplete, but extract the last term
      response( $.ui.autocomplete.filter(
        campos, extractLast( request.term ) ) );
  },
  focus: function( event, ui ) {
	  //var terms = split( this.value );
      // remove the current input
      //terms.pop();
      // add the selected item
      //terms.push( ui.item.value );

      //this.value = terms.join( " " );
      return false;

  },
  select: function( event, ui ) {
	  var terms = split( this.value );
      // remove the current input
      terms.pop();
      // add the selected item
      terms.push( ui.item.value );

      this.value = terms.join( " " );
      
      var busca = document.getElementById("busca");
      busca.selectionEnd = busca.selectionEnd -1;
      
	  if(event.keyCode == 13){
		  itemSelecionado = true;
	  }
      
      return false;

  }
})
.autocomplete( "instance" )._renderItem = function( ul, item ) {
	
  return $( "<li>" )
    .append( "<a>" + item.label + "<br> <span class='detalhesCamposLucene'>" + item.desc + "<br></span></a>" )
        .appendTo( ul );
    };
});

function mascara(o,f){
    v_obj=o
    v_fun=f
    setTimeout("execmascara()",1)
}

function execmascara(){
    v_obj.value=v_fun(v_obj.value)
}

function numeros(v){
    v=v.replace(/\D/g,"")                //Remove tudo o que não é dígito    
    return v
}

function datas(v){
    v=v.replace(/\D/g,"")                    //Remove tudo o que não é dígito
    v=v.replace(/(\d{2})(\d)/,"$1/$2")       //Coloca uma barra entre o segundo e o terceiro dígito
    v=v.replace(/(\d{2})(\d)/,"$1/$2")       //Coloca uma barra entre o segundo e o terceiro dígito
                                            
    
    return v
}

